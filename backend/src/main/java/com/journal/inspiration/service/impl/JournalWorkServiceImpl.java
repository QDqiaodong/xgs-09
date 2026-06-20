package com.journal.inspiration.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.journal.inspiration.dto.WorkElementDTO;
import com.journal.inspiration.dto.WorkPublishDTO;
import com.journal.inspiration.dto.WorkQueryDTO;
import com.journal.inspiration.dto.WorkUpdateDTO;
import com.journal.inspiration.common.ColorAnalysisUtil;
import com.journal.inspiration.common.ColorSchemeValidator;
import com.journal.inspiration.common.CoverTypeEnum;
import com.journal.inspiration.common.ElementCategoryEnum;
import com.journal.inspiration.common.LayoutConfigValidator;
import com.journal.inspiration.common.LayoutTemplateConfig;
import com.journal.inspiration.common.WorkStatusEnum;
import com.journal.inspiration.entity.*;
import com.journal.inspiration.mapper.*;
import com.journal.inspiration.service.*;
import com.journal.inspiration.vo.CategoryStatsVO;
import com.journal.inspiration.vo.CategoryVO;
import com.journal.inspiration.vo.ColorCombinationVO;
import com.journal.inspiration.vo.ColorFamilyStatsVO;
import com.journal.inspiration.vo.ColorUsageVO;
import com.journal.inspiration.vo.SingleColorStatsVO;
import com.journal.inspiration.vo.ScenePreferenceVO;
import com.journal.inspiration.vo.SceneTaskCheckVO;
import com.journal.inspiration.vo.StatusStatsVO;
import com.journal.inspiration.vo.StylePreferenceVO;
import com.journal.inspiration.vo.UserStyleProfileVO;
import com.journal.inspiration.vo.WorkCoverVO;
import com.journal.inspiration.vo.WorkElementGroupVO;
import com.journal.inspiration.vo.WorkElementVO;
import com.journal.inspiration.vo.WorkStatsVO;
import com.journal.inspiration.vo.WorkVO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JournalWorkServiceImpl extends ServiceImpl<JournalWorkMapper, JournalWork> implements JournalWorkService {

    private final WorkCategoryMapper workCategoryMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final FavoriteService favoriteService;
    private final WorkElementMapper workElementMapper;
    private final WorkSceneTaskCheckService workSceneTaskCheckService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"latestWorks", "hotWorks"}, allEntries = true)
    public Long publishWork(WorkPublishDTO dto) {
        User user = userMapper.selectById(dto.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        List<Long> categoryIds = new ArrayList<>();
        if (dto.getCategoryIds() != null) {
            categoryIds = dto.getCategoryIds().stream().distinct().collect(Collectors.toList());
        }
        List<Category> categories = new ArrayList<>();
        if (!categoryIds.isEmpty()) {
            categories = categoryMapper.selectBatchIds(categoryIds);
            if (categories.size() != categoryIds.size()) {
                throw new IllegalArgumentException("存在无效或已失效的分类编号");
            }
        }

        if (dto.getElements() != null && !dto.getElements().isEmpty()) {
            for (WorkElementDTO elementDTO : dto.getElements()) {
                if (ElementCategoryEnum.getByCode(elementDTO.getCategory()) == null) {
                    throw new IllegalArgumentException("存在无效的元素分类: " + elementDTO.getCategory());
                }
            }
        }

        ColorSchemeValidator.ValidationResult colorValidation = ColorSchemeValidator.validate(dto.getColorScheme());
        if (!colorValidation.isValid()) {
            throw new IllegalArgumentException(colorValidation.getMessage());
        }

        LayoutConfigValidator.ValidationResult layoutValidation = LayoutConfigValidator.validate(dto.getLayoutConfig());
        if (!layoutValidation.isValid()) {
            throw new IllegalArgumentException(layoutValidation.getMessage());
        }

        String normalizedColorScheme = ColorSchemeValidator.normalize(dto.getColorScheme());
        String normalizedLayoutConfig = LayoutConfigValidator.normalize(dto.getLayoutConfig());

        JournalWork work = new JournalWork();
        BeanUtil.copyProperties(dto, work);
        work.setColorScheme(normalizedColorScheme);
        if (work.getCoverType() == null || CoverTypeEnum.getByCode(work.getCoverType()) == null) {
            work.setCoverType(CoverTypeEnum.getDefault().getCode());
        }

        if (normalizedLayoutConfig != null && !normalizedLayoutConfig.trim().isEmpty()) {
            work.setLayoutConfig(normalizedLayoutConfig);
        } else if (work.getLayoutConfig() == null || work.getLayoutConfig().trim().isEmpty()) {
            String defaultLayoutJson = LayoutTemplateConfig.getDefaultLayoutJson(categories);
            if (defaultLayoutJson != null) {
                work.setLayoutConfig(defaultLayoutJson);
            }
        }

        work.setViewCount(0);
        work.setLikeCount(0);
        work.setFavoriteCount(0);
        work.setStatus(WorkStatusEnum.PUBLIC.getCode());
        save(work);

        for (Long categoryId : categoryIds) {
            WorkCategory workCategory = new WorkCategory();
            workCategory.setWorkId(work.getId());
            workCategory.setCategoryId(categoryId);
            workCategoryMapper.insert(workCategory);
        }

        if (dto.getElements() != null && !dto.getElements().isEmpty()) {
            int sort = 0;
            for (WorkElementDTO elementDTO : dto.getElements()) {
                WorkElement element = new WorkElement();
                BeanUtil.copyProperties(elementDTO, element);
                element.setWorkId(work.getId());
                if (element.getQuantity() == null || element.getQuantity() < 1) {
                    element.setQuantity(1);
                }
                if (element.getSort() == null) {
                    element.setSort(sort++);
                }
                workElementMapper.insert(element);
            }
        }

        if (dto.getSceneCategoryId() != null) {
            workSceneTaskCheckService.saveCheckList(
                    work.getId(),
                    dto.getUserId(),
                    dto.getSceneCategoryId(),
                    dto.getCheckedSceneTaskIds()
            );
        }

        return work.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"latestWorks", "hotWorks"}, allEntries = true)
    public boolean updateWork(WorkUpdateDTO dto) {
        JournalWork existWork = getById(dto.getId());
        if (existWork == null) {
            throw new IllegalArgumentException("作品不存在");
        }
        if (dto.getUserId() == null || !existWork.getUserId().equals(dto.getUserId())) {
            throw new SecurityException("无权限修改该作品");
        }
        if (WorkStatusEnum.isArchived(existWork.getStatus())) {
            throw new IllegalArgumentException("已归档作品无法修改");
        }

        List<Long> categoryIds = new ArrayList<>();
        if (dto.getCategoryIds() != null) {
            categoryIds = dto.getCategoryIds().stream().distinct().collect(Collectors.toList());
        }
        List<Category> categories = new ArrayList<>();
        if (!categoryIds.isEmpty()) {
            categories = categoryMapper.selectBatchIds(categoryIds);
            if (categories.size() != categoryIds.size()) {
                throw new IllegalArgumentException("存在无效或已失效的分类编号");
            }
        }

        if (dto.getElements() != null && !dto.getElements().isEmpty()) {
            for (WorkElementDTO elementDTO : dto.getElements()) {
                if (ElementCategoryEnum.getByCode(elementDTO.getCategory()) == null) {
                    throw new IllegalArgumentException("存在无效的元素分类: " + elementDTO.getCategory());
                }
            }
        }

        String normalizedColorScheme = null;
        if (dto.getColorScheme() != null) {
            ColorSchemeValidator.ValidationResult colorValidation = ColorSchemeValidator.validate(dto.getColorScheme());
            if (!colorValidation.isValid()) {
                throw new IllegalArgumentException(colorValidation.getMessage());
            }
            normalizedColorScheme = ColorSchemeValidator.normalize(dto.getColorScheme());
        }

        String normalizedLayoutConfig = null;
        if (dto.getLayoutConfig() != null) {
            LayoutConfigValidator.ValidationResult layoutValidation = LayoutConfigValidator.validate(dto.getLayoutConfig());
            if (!layoutValidation.isValid()) {
                throw new IllegalArgumentException(layoutValidation.getMessage());
            }
            normalizedLayoutConfig = LayoutConfigValidator.normalize(dto.getLayoutConfig());
        }

        boolean coverChanged = false;
        String oldCoverImage = existWork.getCoverImage();
        if (dto.getCoverImage() != null && !dto.getCoverImage().equals(oldCoverImage)) {
            coverChanged = true;
        }

        JournalWork work = new JournalWork();
        work.setId(dto.getId());
        if (dto.getTitle() != null) {
            work.setTitle(dto.getTitle());
        }
        if (dto.getCoverImage() != null) {
            work.setCoverImage(dto.getCoverImage());
        }
        if (dto.getCoverType() != null) {
            if (CoverTypeEnum.getByCode(dto.getCoverType()) == null) {
                work.setCoverType(CoverTypeEnum.getDefault().getCode());
            } else {
                work.setCoverType(dto.getCoverType());
            }
        }
        if (dto.getContent() != null) {
            work.setContent(dto.getContent());
        }
        if (dto.getLayoutIdea() != null) {
            work.setLayoutIdea(dto.getLayoutIdea());
        }
        if (dto.getLayoutConfig() != null) {
            if (normalizedLayoutConfig != null && !normalizedLayoutConfig.trim().isEmpty()) {
                work.setLayoutConfig(normalizedLayoutConfig);
            } else if (dto.getLayoutConfig().trim().isEmpty()) {
                String defaultLayoutJson = LayoutTemplateConfig.getDefaultLayoutJson(categories);
                if (defaultLayoutJson != null) {
                    work.setLayoutConfig(defaultLayoutJson);
                }
            } else {
                work.setLayoutConfig(dto.getLayoutConfig());
            }
        }
        if (dto.getColorScheme() != null) {
            work.setColorScheme(normalizedColorScheme);
        }
        if (dto.getInspiration() != null) {
            work.setInspiration(dto.getInspiration());
        }

        boolean updated = updateById(work);
        if (!updated) {
            return false;
        }

        if (coverChanged && StrUtil.isNotBlank(oldCoverImage)) {
            handleOldCoverResource(dto.getId(), oldCoverImage);
        }

        if (dto.getCategoryIds() != null) {
            workCategoryMapper.delete(
                    new LambdaQueryWrapper<WorkCategory>().eq(WorkCategory::getWorkId, dto.getId())
            );
            for (Long categoryId : categoryIds) {
                WorkCategory workCategory = new WorkCategory();
                workCategory.setWorkId(dto.getId());
                workCategory.setCategoryId(categoryId);
                workCategoryMapper.insert(workCategory);
            }
        }

        if (dto.getElements() != null) {
            workElementMapper.delete(
                    new LambdaQueryWrapper<WorkElement>().eq(WorkElement::getWorkId, dto.getId())
            );
            if (!dto.getElements().isEmpty()) {
                int sort = 0;
                for (WorkElementDTO elementDTO : dto.getElements()) {
                    WorkElement element = new WorkElement();
                    BeanUtil.copyProperties(elementDTO, element);
                    element.setWorkId(dto.getId());
                    if (element.getQuantity() == null || element.getQuantity() < 1) {
                        element.setQuantity(1);
                    }
                    if (element.getSort() == null) {
                        element.setSort(sort++);
                    }
                    workElementMapper.insert(element);
                }
            }
        }

        return true;
    }

    private void handleOldCoverResource(Long workId, String oldCoverImage) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String coverHistoryKey = "cover_history_" + workId;
            
            com.fasterxml.jackson.databind.JsonNode existingNode = null;
            try {
                String existingHistory = getCoverHistoryFromDatabase(workId);
                if (existingHistory != null) {
                    existingNode = mapper.readTree(existingHistory);
                }
            } catch (Exception e) {
                existingNode = mapper.createArrayNode();
            }

            com.fasterxml.jackson.databind.node.ArrayNode historyArray;
            if (existingNode != null && existingNode.isArray()) {
                historyArray = (com.fasterxml.jackson.databind.node.ArrayNode) existingNode;
            } else {
                historyArray = mapper.createArrayNode();
            }

            com.fasterxml.jackson.databind.node.ObjectNode historyItem = mapper.createObjectNode();
            historyItem.put("url", oldCoverImage);
            historyItem.put("replaceTime", java.time.LocalDateTime.now().toString());
            historyItem.put("status", "replaced");
            historyArray.add(historyItem);

            String newHistory = mapper.writeValueAsString(historyArray);
            saveCoverHistoryToDatabase(workId, newHistory);
        } catch (Exception e) {
        }
    }

    private String getCoverHistoryFromDatabase(Long workId) {
        return null;
    }

    private void saveCoverHistoryToDatabase(Long workId, String history) {
    }

    @Override
    public WorkVO getWorkDetail(Long id, Long userId) {
        JournalWork work = getById(id);
        if (work == null) {
            return null;
        }

        WorkVO vo = convertToVO(work);

        if (vo.getCategories() != null && !vo.getCategories().isEmpty()) {
            Long sceneCategoryId = vo.getCategories().stream()
                    .filter(cat -> "scene".equals(cat.getType()))
                    .map(CategoryVO::getId)
                    .findFirst()
                    .orElse(null);
            if (sceneCategoryId != null) {
                List<SceneTaskCheckVO> checkList = workSceneTaskCheckService.getCheckListByWorkId(id, sceneCategoryId);
                vo.setSceneTaskCheckList(checkList);
                vo.setSceneTaskTotal(checkList.size());
                vo.setSceneTaskChecked((int) checkList.stream().filter(item -> item.getChecked() != null && item.getChecked() == 1).count());
            }
        }

        if (userId != null) {
            vo.setIsFavorite(favoriteService.isFavorite(userId, id));
        }

        return vo;
    }

    @Override
    public Page<WorkVO> getWorkList(WorkQueryDTO dto) {
        LambdaQueryWrapper<JournalWork> wrapper = new LambdaQueryWrapper<>();

        if (dto.getStatus() != null) {
            wrapper.eq(JournalWork::getStatus, dto.getStatus());
        } else if (dto.getUserId() == null) {
            wrapper.eq(JournalWork::getStatus, WorkStatusEnum.PUBLIC.getCode());
        }

        if (StrUtil.isNotBlank(dto.getKeyword())) {
            wrapper.and(w -> w.like(JournalWork::getTitle, dto.getKeyword())
                    .or().like(JournalWork::getContent, dto.getKeyword()));
        }

        if (dto.getUserId() != null) {
            wrapper.eq(JournalWork::getUserId, dto.getUserId());
        } else {
            wrapper.isNotNull(JournalWork::getCoverImage);
            wrapper.ne(JournalWork::getCoverImage, "");
        }

        List<Long> filteredWorkIds = getFilteredWorkIds(dto);
        if (filteredWorkIds != null) {
            if (filteredWorkIds.isEmpty()) {
                return new Page<>(dto.getPageNum(), dto.getPageSize(), 0);
            }
            wrapper.in(JournalWork::getId, filteredWorkIds);
        }

        wrapper.orderByDesc(JournalWork::getCreateTime);

        Page<JournalWork> page = page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        Page<WorkVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(this::convertToVO).collect(Collectors.toList()));

        return voPage;
    }

    private List<Long> getFilteredWorkIds(WorkQueryDTO dto) {
        List<Long> specificCategoryIds = new ArrayList<>();
        if (dto.getCategoryId() != null) {
            specificCategoryIds.add(dto.getCategoryId());
        }
        if (dto.getStyleCategoryId() != null) {
            specificCategoryIds.add(dto.getStyleCategoryId());
        }
        if (dto.getSceneCategoryId() != null) {
            specificCategoryIds.add(dto.getSceneCategoryId());
        }

        Set<Long> validCategoryIds = null;
        if (!specificCategoryIds.isEmpty()) {
            List<Category> existing = categoryMapper.selectBatchIds(specificCategoryIds);
            validCategoryIds = existing.stream().map(Category::getId).collect(Collectors.toSet());
        }

        List<List<Long>> dimensionWorkIds = new ArrayList<>();
        if (dto.getCategoryId() != null) {
            dimensionWorkIds.add(workIdsByCategory(dto.getCategoryId(), validCategoryIds));
        }
        if (dto.getStyleCategoryId() != null) {
            dimensionWorkIds.add(workIdsByCategory(dto.getStyleCategoryId(), validCategoryIds));
        }
        if (dto.getSceneCategoryId() != null) {
            dimensionWorkIds.add(workIdsByCategory(dto.getSceneCategoryId(), validCategoryIds));
        }

        if (dto.getCategoryType() != null) {
            List<Long> typeCategoryIds = categoryMapper.selectList(
                    new LambdaQueryWrapper<Category>().eq(Category::getType, dto.getCategoryType())
            ).stream().map(Category::getId).collect(Collectors.toList());

            if (typeCategoryIds.isEmpty()) {
                dimensionWorkIds.add(new ArrayList<>());
            } else {
                List<Long> typeWorkIds = workCategoryMapper.selectList(
                        new LambdaQueryWrapper<WorkCategory>().in(WorkCategory::getCategoryId, typeCategoryIds)
                ).stream().map(WorkCategory::getWorkId).distinct().collect(Collectors.toList());
                dimensionWorkIds.add(typeWorkIds);
            }
        }

        if (dimensionWorkIds.isEmpty()) {
            return null;
        }

        List<Long> result = new ArrayList<>(dimensionWorkIds.get(0));
        for (int i = 1; i < dimensionWorkIds.size(); i++) {
            Set<Long> other = new HashSet<>(dimensionWorkIds.get(i));
            result.removeIf(id -> !other.contains(id));
        }
        return result;
    }

    private List<Long> workIdsByCategory(Long categoryId, Set<Long> validCategoryIds) {
        if (validCategoryIds != null && !validCategoryIds.contains(categoryId)) {
            return new ArrayList<>();
        }
        return workCategoryMapper.selectList(
                new LambdaQueryWrapper<WorkCategory>().eq(WorkCategory::getCategoryId, categoryId)
        ).stream().map(WorkCategory::getWorkId).distinct().collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "latestWorks", key = "#pageNum + '_' + #pageSize")
    public Page<WorkVO> getLatestWorks(Integer pageNum, Integer pageSize) {
        WorkQueryDTO dto = new WorkQueryDTO();
        dto.setPageNum(pageNum);
        dto.setPageSize(pageSize);
        dto.setStatus(WorkStatusEnum.PUBLIC.getCode());
        return getWorkList(dto);
    }

    @Override
    @Cacheable(value = "hotWorks", key = "#pageNum + '_' + #pageSize")
    public Page<WorkVO> getHotWorks(Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<JournalWork> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JournalWork::getStatus, WorkStatusEnum.PUBLIC.getCode());
        wrapper.orderByDesc(JournalWork::getViewCount, JournalWork::getFavoriteCount);

        Page<JournalWork> page = page(new Page<>(pageNum, pageSize), wrapper);
        Page<WorkVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(this::convertToVO).collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public Page<WorkVO> getUserWorks(Long userId, Integer pageNum, Integer pageSize) {
        WorkQueryDTO dto = new WorkQueryDTO();
        dto.setUserId(userId);
        dto.setPageNum(pageNum);
        dto.setPageSize(pageSize);
        return getWorkList(dto);
    }

    @Override
    @CacheEvict(value = {"latestWorks", "hotWorks"}, allEntries = true)
    public boolean updateWorkStatus(Long id, Integer status, Long operatorId) {
        if (WorkStatusEnum.getByCode(status) == null) {
            throw new IllegalArgumentException("非法的状态值: " + status);
        }
        JournalWork existWork = getById(id);
        if (existWork == null) {
            throw new IllegalArgumentException("作品不存在");
        }
        if (operatorId == null || !existWork.getUserId().equals(operatorId)) {
            throw new SecurityException("无权限修改该作品状态");
        }

        boolean isArchiving = WorkStatusEnum.isArchived(status) && !WorkStatusEnum.isArchived(existWork.getStatus());
        String oldCoverImage = existWork.getCoverImage();

        JournalWork work = new JournalWork();
        work.setId(id);
        work.setStatus(status);

        if (isArchiving) {
            work.setCoverImage(null);
        }

        boolean updated = updateById(work);
        if (!updated) {
            return false;
        }

        if (isArchiving && StrUtil.isNotBlank(oldCoverImage)) {
            handleArchivedCoverResource(id, oldCoverImage);
        }

        return true;
    }

    private void handleArchivedCoverResource(Long workId, String oldCoverImage) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

            com.fasterxml.jackson.databind.JsonNode existingNode = null;
            try {
                String existingHistory = getCoverHistoryFromDatabase(workId);
                if (existingHistory != null) {
                    existingNode = mapper.readTree(existingHistory);
                }
            } catch (Exception e) {
                existingNode = mapper.createArrayNode();
            }

            com.fasterxml.jackson.databind.node.ArrayNode historyArray;
            if (existingNode != null && existingNode.isArray()) {
                historyArray = (com.fasterxml.jackson.databind.node.ArrayNode) existingNode;
            } else {
                historyArray = mapper.createArrayNode();
            }

            com.fasterxml.jackson.databind.node.ObjectNode historyItem = mapper.createObjectNode();
            historyItem.put("url", oldCoverImage);
            historyItem.put("archiveTime", java.time.LocalDateTime.now().toString());
            historyItem.put("status", "archived");
            historyArray.add(historyItem);

            String newHistory = mapper.writeValueAsString(historyArray);
            saveCoverHistoryToDatabase(workId, newHistory);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean incrementViewCount(Long id) {
        return baseMapper.incrementViewCountById(id) > 0;
    }

    @Override
    public WorkStatsVO getUserWorkStats(Long userId) {
        WorkStatsVO stats = new WorkStatsVO();

        LambdaQueryWrapper<JournalWork> workWrapper = new LambdaQueryWrapper<>();
        workWrapper.eq(JournalWork::getUserId, userId);
        List<JournalWork> allWorks = list(workWrapper);

        int total = allWorks.size();
        int publicCount = 0;
        int privateCount = 0;
        int archivedCount = 0;

        for (JournalWork work : allWorks) {
            if (WorkStatusEnum.isPublic(work.getStatus())) {
                publicCount++;
            } else if (WorkStatusEnum.isPrivate(work.getStatus())) {
                privateCount++;
            } else if (WorkStatusEnum.isArchived(work.getStatus())) {
                archivedCount++;
            }
        }

        stats.setTotalWorks(total);
        stats.setPublicWorks(publicCount);
        stats.setPrivateWorks(privateCount);
        stats.setArchivedWorks(archivedCount);

        List<StatusStatsVO> statusStatsList = new java.util.ArrayList<>();
        for (WorkStatusEnum statusEnum : WorkStatusEnum.values()) {
            StatusStatsVO statusStats = new StatusStatsVO();
            statusStats.setStatus(statusEnum.getCode());
            statusStats.setStatusDesc(statusEnum.getDesc());
            int count = 0;
            if (WorkStatusEnum.PUBLIC.equals(statusEnum)) {
                count = publicCount;
            } else if (WorkStatusEnum.PRIVATE.equals(statusEnum)) {
                count = privateCount;
            } else if (WorkStatusEnum.ARCHIVED.equals(statusEnum)) {
                count = archivedCount;
            }
            statusStats.setCount(count);
            statusStatsList.add(statusStats);
        }
        stats.setStatusStats(statusStatsList);

        if (!allWorks.isEmpty()) {
            List<Long> workIds = allWorks.stream()
                    .map(JournalWork::getId)
                    .collect(Collectors.toList());

            List<WorkCategory> workCategories = workCategoryMapper.selectList(
                    new LambdaQueryWrapper<WorkCategory>().in(WorkCategory::getWorkId, workIds)
            );

            if (!workCategories.isEmpty()) {
                List<Long> categoryIds = workCategories.stream()
                        .map(WorkCategory::getCategoryId)
                        .distinct()
                        .collect(Collectors.toList());

                List<Category> categories = categoryMapper.selectBatchIds(categoryIds);
                Set<Long> validCategoryIds = categories.stream()
                        .map(Category::getId)
                        .collect(Collectors.toSet());

                java.util.Map<Long, Long> categoryCountMap = workCategories.stream()
                        .filter(wc -> validCategoryIds.contains(wc.getCategoryId()))
                        .collect(Collectors.groupingBy(
                                WorkCategory::getCategoryId,
                                Collectors.mapping(WorkCategory::getWorkId, Collectors.toSet())))
                        .entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> (long) e.getValue().size()));

                List<CategoryStatsVO> styleStats = new java.util.ArrayList<>();
                List<CategoryStatsVO> sceneStats = new java.util.ArrayList<>();

                for (Category category : categories) {
                    CategoryStatsVO categoryStats = new CategoryStatsVO();
                    categoryStats.setId(category.getId());
                    categoryStats.setName(category.getName());
                    categoryStats.setType(category.getType());
                    categoryStats.setCount(categoryCountMap.getOrDefault(category.getId(), 0L).intValue());

                    if ("style".equals(category.getType())) {
                        styleStats.add(categoryStats);
                    } else if ("scene".equals(category.getType())) {
                        sceneStats.add(categoryStats);
                    }
                }

                styleStats.sort((a, b) -> b.getCount() - a.getCount());
                sceneStats.sort((a, b) -> b.getCount() - a.getCount());

                stats.setStyleStats(styleStats);
                stats.setSceneStats(sceneStats);
            }
        }

        stats.setColorUsage(analyzeColorUsage(allWorks));

        if (stats.getStyleStats() == null) {
            stats.setStyleStats(new java.util.ArrayList<>());
        }
        if (stats.getSceneStats() == null) {
            stats.setSceneStats(new java.util.ArrayList<>());
        }

        return stats;
    }

    private ColorUsageVO analyzeColorUsage(List<JournalWork> allWorks) {
        ColorUsageVO colorUsage = new ColorUsageVO();

        List<String> allColorSchemes = allWorks.stream()
                .map(JournalWork::getColorScheme)
                .filter(cs -> cs != null && !cs.trim().isEmpty())
                .collect(Collectors.toList());

        colorUsage.setTotalColorSchemes(allColorSchemes.size());

        if (allColorSchemes.isEmpty()) {
            colorUsage.setFamilyStats(new java.util.ArrayList<>());
            colorUsage.setTopColors(new java.util.ArrayList<>());
            colorUsage.setTopCombinations(new java.util.ArrayList<>());
            return colorUsage;
        }

        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        List<List<java.util.Map<String, Object>>> allSwatchesList = new ArrayList<>();
        List<List<String>> allColorCombinations = new ArrayList<>();

        for (String colorScheme : allColorSchemes) {
            try {
                java.util.List<java.util.Map<String, Object>> swatches = mapper.readValue(
                        colorScheme,
                        new com.fasterxml.jackson.core.type.TypeReference<java.util.List<java.util.Map<String, Object>>>() {}
                );
                if (swatches != null && !swatches.isEmpty()) {
                    allSwatchesList.add(swatches);
                    List<String> colors = swatches.stream()
                            .map(s -> s.get("color"))
                            .filter(Objects::nonNull)
                            .map(Object::toString)
                            .filter(c -> c != null && !c.isEmpty())
                            .collect(Collectors.toList());
                    if (!colors.isEmpty()) {
                        allColorCombinations.add(colors);
                    }
                }
            } catch (Exception e) {
            }
        }

        colorUsage.setFamilyStats(analyzeFamilyStats(allSwatchesList));
        colorUsage.setTopColors(analyzeTopColors(allSwatchesList));
        colorUsage.setTopCombinations(analyzeTopCombinations(allColorCombinations));

        return colorUsage;
    }

    private List<ColorFamilyStatsVO> analyzeFamilyStats(
            List<List<java.util.Map<String, Object>>> allSwatchesList) {

        Map<String, Integer> familyCountMap = new LinkedHashMap<>();
        Map<String, String> familyRepColorMap = new LinkedHashMap<>();

        for (List<java.util.Map<String, Object>> swatches : allSwatchesList) {
            for (java.util.Map<String, Object> swatch : swatches) {
                Object colorObj = swatch.get("color");
                if (colorObj == null) continue;
                String color = colorObj.toString();
                ColorAnalysisUtil.ColorFamily family = ColorAnalysisUtil.classifyColor(color);
                String familyKey = family.getKey();
                familyCountMap.merge(familyKey, 1, Integer::sum);
                familyRepColorMap.putIfAbsent(familyKey, family.getRepresentativeColor());
            }
        }

        int totalColors = familyCountMap.values().stream().mapToInt(Integer::intValue).sum();

        List<ColorFamilyStatsVO> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : familyCountMap.entrySet()) {
            String familyKey = entry.getKey();
            ColorFamilyStatsVO vo = new ColorFamilyStatsVO();
            ColorAnalysisUtil.ColorFamily family = ColorAnalysisUtil.getAllFamilies().stream()
                    .filter(f -> f.getKey().equals(familyKey))
                    .findFirst()
                    .orElse(null);
            if (family == null) continue;

            vo.setFamily(familyKey);
            vo.setFamilyName(family.getName());
            vo.setRepresentativeColor(familyRepColorMap.get(familyKey));
            vo.setCount(entry.getValue());
            vo.setPercentage(totalColors > 0 ? (entry.getValue() * 100.0 / totalColors) : 0);
            result.add(vo);
        }

        result.sort((a, b) -> b.getCount() - a.getCount());
        return result;
    }

    private List<SingleColorStatsVO> analyzeTopColors(
            List<List<java.util.Map<String, Object>>> allSwatchesList) {

        Map<String, Integer> colorCountMap = new LinkedHashMap<>();
        Map<String, String> colorNameMap = new LinkedHashMap<>();
        Map<String, String> colorUsageTypeMap = new LinkedHashMap<>();

        for (List<java.util.Map<String, Object>> swatches : allSwatchesList) {
            for (java.util.Map<String, Object> swatch : swatches) {
                Object colorObj = swatch.get("color");
                if (colorObj == null) continue;
                String color = ColorAnalysisUtil.normalizeColor(colorObj.toString());
                if (color == null) continue;

                boolean foundSimilar = false;
                for (String existingColor : colorCountMap.keySet()) {
                    if (ColorAnalysisUtil.areColorsSimilar(color, existingColor, 35)) {
                        colorCountMap.merge(existingColor, 1, Integer::sum);
                        foundSimilar = true;
                        break;
                    }
                }
                if (!foundSimilar) {
                    colorCountMap.put(color, 1);
                    Object nameObj = swatch.get("name");
                    String colorName = nameObj != null ? nameObj.toString() : ColorAnalysisUtil.getColorName(color);
                    colorNameMap.put(color, colorName);
                    Object typeObj = swatch.get("type");
                    if (typeObj == null) {
                        Object purposeObj = swatch.get("purpose");
                        String purpose = purposeObj != null ? purposeObj.toString() : "";
                        if (purpose.contains("主色") || purpose.contains("primary")) {
                            colorUsageTypeMap.put(color, "主色");
                        } else if (purpose.contains("辅助") || purpose.contains("secondary")) {
                            colorUsageTypeMap.put(color, "辅助色");
                        } else if (purpose.contains("点缀") || purpose.contains("accent")) {
                            colorUsageTypeMap.put(color, "点缀色");
                        } else {
                            colorUsageTypeMap.put(color, "配色");
                        }
                    } else {
                        String type = typeObj.toString();
                        if ("primary".equals(type)) {
                            colorUsageTypeMap.put(color, "主色");
                        } else if ("secondary".equals(type)) {
                            colorUsageTypeMap.put(color, "辅助色");
                        } else if ("accent".equals(type)) {
                            colorUsageTypeMap.put(color, "点缀色");
                        } else {
                            colorUsageTypeMap.put(color, "配色");
                        }
                    }
                }
            }
        }

        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(colorCountMap.entrySet());
        sortedEntries.sort((a, b) -> b.getValue() - a.getValue());

        List<SingleColorStatsVO> result = new ArrayList<>();
        int limit = Math.min(8, sortedEntries.size());
        for (int i = 0; i < limit; i++) {
            Map.Entry<String, Integer> entry = sortedEntries.get(i);
            SingleColorStatsVO vo = new SingleColorStatsVO();
            vo.setColor(entry.getKey());
            vo.setName(colorNameMap.get(entry.getKey()));
            vo.setCount(entry.getValue());
            vo.setUsageType(colorUsageTypeMap.get(entry.getKey()));
            result.add(vo);
        }

        return result;
    }

    private List<ColorCombinationVO> analyzeTopCombinations(List<List<String>> allColorCombinations) {

        Map<String, ColorCombinationVO> combinationMap = new LinkedHashMap<>();

        for (List<String> colors : allColorCombinations) {
            if (colors.size() < 2) continue;

            List<String> normalizedColors = ColorAnalysisUtil.generateCombinationKey(colors);
            if (normalizedColors.size() < 2) continue;

            String key = String.join("|", normalizedColors);

            ColorCombinationVO existing = combinationMap.get(key);
            if (existing != null) {
                existing.setCount(existing.getCount() + 1);
            } else {
                ColorCombinationVO vo = new ColorCombinationVO();
                vo.setColors(new ArrayList<>(normalizedColors));
                vo.setCount(1);
                vo.setStyleTag(ColorAnalysisUtil.inferStyleTag(normalizedColors));
                combinationMap.put(key, vo);
            }
        }

        List<ColorCombinationVO> result = new ArrayList<>(combinationMap.values());
        result.sort((a, b) -> b.getCount() - a.getCount());

        return result.stream().limit(5).collect(Collectors.toList());
    }

    private WorkVO convertToVO(JournalWork work) {
        WorkVO vo = new WorkVO();
        BeanUtil.copyProperties(work, vo);

        CoverTypeEnum coverType = CoverTypeEnum.getByCode(work.getCoverType());
        if (coverType == null) {
            coverType = CoverTypeEnum.getDefault();
        }
        vo.setCoverType(coverType.getCode());
        vo.setCoverTypeDesc(coverType.getDesc());
        vo.setCoverWidthRatio(coverType.getWidthRatio());
        vo.setCoverHeightRatio(coverType.getHeightRatio());
        vo.setCoverAspectRatio(coverType.getAspectRatio());

        User user = userMapper.selectById(work.getUserId());
        if (user != null) {
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
        }

        List<WorkCategory> workCategories = workCategoryMapper.selectList(
                new LambdaQueryWrapper<WorkCategory>().eq(WorkCategory::getWorkId, work.getId())
        );

        if (!workCategories.isEmpty()) {
            List<Long> categoryIds = workCategories.stream()
                    .map(WorkCategory::getCategoryId)
                    .collect(Collectors.toList());

            List<Category> categories = categoryMapper.selectBatchIds(categoryIds);
            List<CategoryVO> categoryVOS = categories.stream()
                    .map(cat -> {
                        CategoryVO catVO = new CategoryVO();
                        BeanUtil.copyProperties(cat, catVO);
                        return catVO;
                    })
                    .collect(Collectors.toList());
            vo.setCategories(categoryVOS);
        }

        if (StrUtil.isNotBlank(work.getColorScheme())) {
            String normalized = ColorSchemeValidator.normalize(work.getColorScheme());
            if (normalized != null) {
                vo.setColorScheme(enrichColorSchemeWithType(normalized));
            } else {
                vo.setColorScheme(null);
            }
        }

        vo.setElementGroups(getWorkElementGroups(work.getId()));

        vo.setIsFavorite(false);
        return vo;
    }

    private List<WorkElementGroupVO> getWorkElementGroups(Long workId) {
        List<WorkElement> elements = workElementMapper.selectList(
                new LambdaQueryWrapper<WorkElement>()
                        .eq(WorkElement::getWorkId, workId)
                        .orderByAsc(WorkElement::getCategory, WorkElement::getSort)
        );

        if (elements.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Integer, List<WorkElement>> groupMap = elements.stream()
                .collect(Collectors.groupingBy(WorkElement::getCategory));

        List<WorkElementGroupVO> groupVOList = new ArrayList<>();

        for (ElementCategoryEnum categoryEnum : ElementCategoryEnum.values()) {
            List<WorkElement> categoryElements = groupMap.get(categoryEnum.getCode());
            if (categoryElements != null && !categoryElements.isEmpty()) {
                WorkElementGroupVO groupVO = new WorkElementGroupVO();
                groupVO.setCategory(categoryEnum.getCode());
                groupVO.setCategoryDesc(categoryEnum.getDesc());
                groupVO.setCategoryIcon(categoryEnum.getIcon());

                List<WorkElementVO> elementVOList = categoryElements.stream()
                        .sorted(Comparator.comparing(WorkElement::getSort, Comparator.nullsLast(Integer::compareTo)))
                        .map(el -> {
                            WorkElementVO evo = new WorkElementVO();
                            BeanUtil.copyProperties(el, evo);
                            evo.setCategoryDesc(categoryEnum.getDesc());
                            evo.setCategoryIcon(categoryEnum.getIcon());
                            return evo;
                        })
                        .collect(Collectors.toList());

                groupVO.setElements(elementVOList);

                int totalCount = elementVOList.stream()
                        .mapToInt(evo -> evo.getQuantity() != null ? evo.getQuantity() : 0)
                        .sum();
                groupVO.setTotalCount(totalCount);

                groupVOList.add(groupVO);
            }
        }

        return groupVOList;
    }

    private String enrichColorSchemeWithType(String colorSchemeJson) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.List<java.util.Map<String, Object>> swatches = mapper.readValue(
                    colorSchemeJson,
                    new com.fasterxml.jackson.core.type.TypeReference<java.util.List<java.util.Map<String, Object>>>() {}
            );

            if (swatches == null || swatches.isEmpty()) {
                return colorSchemeJson;
            }

            boolean hasType = swatches.stream().anyMatch(s -> s.get("type") != null);
            if (hasType) {
                return colorSchemeJson;
            }

            for (java.util.Map<String, Object> swatch : swatches) {
                Object purposeObj = swatch.get("purpose");
                Object nameObj = swatch.get("name");
                String purpose = purposeObj instanceof String ? (String) purposeObj : "";
                String name = nameObj instanceof String ? (String) nameObj : "";
                String combined = (purpose + " " + name).toLowerCase();

                String inferredType = inferColorType(combined);
                if (inferredType != null) {
                    swatch.put("type", inferredType);
                }
            }

            return mapper.writeValueAsString(swatches);
        } catch (Exception e) {
            return colorSchemeJson;
        }
    }

    private String inferColorType(String text) {
        if (text.contains("主色") || text.contains("主色调") || text.contains("primary")) {
            return ColorSchemeValidator.TYPE_PRIMARY;
        }
        if (text.contains("辅助色") || text.contains("次要") || text.contains("secondary")) {
            return ColorSchemeValidator.TYPE_SECONDARY;
        }
        if (text.contains("点缀色") || text.contains("强调色") || text.contains("亮点") || text.contains("accent")) {
            return ColorSchemeValidator.TYPE_ACCENT;
        }
        if (text.contains("背景")) {
            return ColorSchemeValidator.TYPE_SECONDARY;
        }
        return null;
    }

    @Override
    public UserStyleProfileVO getUserStyleProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        LambdaQueryWrapper<JournalWork> workWrapper = new LambdaQueryWrapper<>();
        workWrapper.eq(JournalWork::getUserId, userId);
        workWrapper.eq(JournalWork::getStatus, WorkStatusEnum.PUBLIC.getCode());
        workWrapper.orderByDesc(JournalWork::getFavoriteCount, JournalWork::getViewCount, JournalWork::getCreateTime);
        List<JournalWork> allPublicWorks = list(workWrapper);

        UserStyleProfileVO profile = new UserStyleProfileVO();
        profile.setUserId(userId);
        profile.setNickname(user.getNickname());
        profile.setAvatar(user.getAvatar());
        profile.setBio(user.getBio());
        profile.setTotalWorks((int) count(new LambdaQueryWrapper<JournalWork>().eq(JournalWork::getUserId, userId)));
        profile.setTotalPublicWorks(allPublicWorks.size());

        if (allPublicWorks.isEmpty()) {
            profile.setStylePreferences(new ArrayList<>());
            profile.setScenePreferences(new ArrayList<>());
            profile.setFeaturedWorks(new ArrayList<>());
            return profile;
        }

        List<Long> workIds = allPublicWorks.stream()
                .map(JournalWork::getId)
                .collect(Collectors.toList());

        List<WorkCategory> workCategories = workCategoryMapper.selectList(
                new LambdaQueryWrapper<WorkCategory>().in(WorkCategory::getWorkId, workIds)
        );

        Map<Long, List<Long>> workToCategoryIdsMap = new java.util.HashMap<>();
        Map<Long, List<Long>> categoryToWorkIdsMap = new java.util.HashMap<>();
        for (WorkCategory wc : workCategories) {
            workToCategoryIdsMap.computeIfAbsent(wc.getWorkId(), k -> new ArrayList<>()).add(wc.getCategoryId());
            categoryToWorkIdsMap.computeIfAbsent(wc.getCategoryId(), k -> new ArrayList<>()).add(wc.getWorkId());
        }

        Set<Long> allCategoryIds = new java.util.HashSet<>(categoryToWorkIdsMap.keySet());
        List<Category> allCategories = categoryMapper.selectBatchIds(new ArrayList<>(allCategoryIds));
        Map<Long, Category> categoryMap = allCategories.stream()
                .collect(Collectors.toMap(Category::getId, c -> c));

        Map<Long, JournalWork> workMap = allPublicWorks.stream()
                .collect(Collectors.toMap(JournalWork::getId, w -> w));

        List<StylePreferenceVO> stylePreferences = buildStylePreferences(
                allCategories, categoryToWorkIdsMap, workMap, allPublicWorks.size()
        );
        List<ScenePreferenceVO> scenePreferences = buildScenePreferences(
                allCategories, categoryToWorkIdsMap, workMap, allPublicWorks.size()
        );

        profile.setStylePreferences(stylePreferences);
        profile.setScenePreferences(scenePreferences);

        ColorUsageVO colorTendency = analyzeColorUsage(allPublicWorks);
        profile.setColorTendency(colorTendency);

        List<WorkCoverVO> featuredWorks = buildFeaturedWorks(allPublicWorks);
        profile.setFeaturedWorks(featuredWorks);

        if (!stylePreferences.isEmpty()) {
            profile.setSignatureStyle(stylePreferences.get(0).getName());
        }

        if (colorTendency != null && colorTendency.getTopColors() != null && !colorTendency.getTopColors().isEmpty()) {
            profile.setDominantColor(colorTendency.getTopColors().get(0).getColor());
        }

        return profile;
    }

    private List<StylePreferenceVO> buildStylePreferences(
            List<Category> allCategories,
            Map<Long, List<Long>> categoryToWorkIdsMap,
            Map<Long, JournalWork> workMap,
            int totalWorks
    ) {
        List<StylePreferenceVO> result = new ArrayList<>();
        List<Category> styleCategories = allCategories.stream()
                .filter(c -> "style".equals(c.getType()))
                .collect(Collectors.toList());

        for (Category category : styleCategories) {
            List<Long> workIds = categoryToWorkIdsMap.getOrDefault(category.getId(), new ArrayList<>());
            if (workIds.isEmpty()) continue;

            StylePreferenceVO vo = new StylePreferenceVO();
            vo.setCategoryId(category.getId());
            vo.setName(category.getName());
            vo.setType(category.getType());
            vo.setCount(workIds.size());
            vo.setTotalWorks(totalWorks);
            vo.setPercentage(totalWorks > 0 ? (workIds.size() * 100.0 / totalWorks) : 0);
            vo.setIcon(category.getIcon());
            vo.setDescription(null);
            vo.setBannerColor(null);

            List<WorkCoverVO> representativeWorks = buildRepresentativeWorks(workIds, workMap, 3);
            vo.setRepresentativeWorks(representativeWorks);

            result.add(vo);
        }

        result.sort((a, b) -> b.getCount() - a.getCount());
        return result.stream().limit(5).collect(Collectors.toList());
    }

    private List<ScenePreferenceVO> buildScenePreferences(
            List<Category> allCategories,
            Map<Long, List<Long>> categoryToWorkIdsMap,
            Map<Long, JournalWork> workMap,
            int totalWorks
    ) {
        List<ScenePreferenceVO> result = new ArrayList<>();
        List<Category> sceneCategories = allCategories.stream()
                .filter(c -> "scene".equals(c.getType()))
                .collect(Collectors.toList());

        for (Category category : sceneCategories) {
            List<Long> workIds = categoryToWorkIdsMap.getOrDefault(category.getId(), new ArrayList<>());
            if (workIds.isEmpty()) continue;

            ScenePreferenceVO vo = new ScenePreferenceVO();
            vo.setCategoryId(category.getId());
            vo.setName(category.getName());
            vo.setType(category.getType());
            vo.setCount(workIds.size());
            vo.setTotalWorks(totalWorks);
            vo.setPercentage(totalWorks > 0 ? (workIds.size() * 100.0 / totalWorks) : 0);
            vo.setIcon(category.getIcon());
            vo.setDescription(null);
            vo.setBannerColor(null);

            List<WorkCoverVO> representativeWorks = buildRepresentativeWorks(workIds, workMap, 3);
            vo.setRepresentativeWorks(representativeWorks);

            result.add(vo);
        }

        result.sort((a, b) -> b.getCount() - a.getCount());
        return result.stream().limit(5).collect(Collectors.toList());
    }

    private List<WorkCoverVO> buildRepresentativeWorks(
            List<Long> workIds,
            Map<Long, JournalWork> workMap,
            int limit
    ) {
        List<JournalWork> works = workIds.stream()
                .map(workMap::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt((JournalWork w) -> w.getFavoriteCount() == null ? 0 : w.getFavoriteCount())
                        .thenComparingInt(w -> w.getViewCount() == null ? 0 : w.getViewCount())
                        .reversed())
                .limit(limit)
                .collect(Collectors.toList());

        return works.stream()
                .map(this::convertToWorkCoverVO)
                .collect(Collectors.toList());
    }

    private List<WorkCoverVO> buildFeaturedWorks(List<JournalWork> allWorks) {
        return allWorks.stream()
                .sorted(Comparator.comparingInt((JournalWork w) -> w.getFavoriteCount() == null ? 0 : w.getFavoriteCount())
                        .thenComparingInt(w -> w.getViewCount() == null ? 0 : w.getViewCount())
                        .reversed())
                .limit(6)
                .map(this::convertToWorkCoverVO)
                .collect(Collectors.toList());
    }

    private WorkCoverVO convertToWorkCoverVO(JournalWork work) {
        WorkCoverVO vo = new WorkCoverVO();
        vo.setWorkId(work.getId());
        vo.setTitle(work.getTitle());
        vo.setCoverImage(work.getCoverImage());
        vo.setViewCount(work.getViewCount());
        vo.setFavoriteCount(work.getFavoriteCount());
        vo.setCreateTime(work.getCreateTime());

        CoverTypeEnum coverType = CoverTypeEnum.getByCode(work.getCoverType());
        if (coverType == null) {
            coverType = CoverTypeEnum.getDefault();
        }
        vo.setCoverType(coverType.getCode());
        vo.setCoverTypeDesc(coverType.getDesc());
        vo.setCoverWidthRatio(coverType.getWidthRatio());
        vo.setCoverHeightRatio(coverType.getHeightRatio());
        vo.setCoverAspectRatio(coverType.getAspectRatio());

        return vo;
    }
}
