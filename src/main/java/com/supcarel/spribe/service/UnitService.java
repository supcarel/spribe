package com.supcarel.spribe.service;

import com.supcarel.spribe.exception.ResourceNotFoundException;
import com.supcarel.spribe.mapper.UnitMapper;
import com.supcarel.spribe.mapper.UnitTypeMapper;
import com.supcarel.spribe.model.Unit;
import com.supcarel.spribe.model.UnitType;
import com.supcarel.spribe.model.User;
import com.supcarel.spribe.model.enums.BookingStatusEnum;
import com.supcarel.spribe.payload.request.UnitRequest;
import com.supcarel.spribe.payload.request.UnitSearchRequest;
import com.supcarel.spribe.payload.response.PageableResponse;
import com.supcarel.spribe.payload.response.UnitResponse;
import com.supcarel.spribe.payload.response.UnitTypeResponse;
import com.supcarel.spribe.repository.UnitRepository;
import com.supcarel.spribe.repository.UnitTypeRepository;
import com.supcarel.spribe.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Service
public class UnitService {

    private final UserRepository userRepository;
    private final UnitRepository unitRepository;
    private final UnitTypeRepository unitTypeRepository;
    private final PriceCalculator priceCalculator;

    public UnitService(UserRepository userRepository, UnitRepository unitRepository, UnitTypeRepository unitTypeRepository, PriceCalculator priceCalculator) {
        this.userRepository = userRepository;
        this.unitRepository = unitRepository;
        this.unitTypeRepository = unitTypeRepository;
        this.priceCalculator = priceCalculator;
    }

    @Transactional
    public UnitResponse createUnit(UnitRequest unitRequest, UUID userId) {
        log.info("Create unit request user: {}", userId);

        Unit unit = UnitMapper.MAPPER.mapRequestToEntity(unitRequest);

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        unit.setOwner(user);

        UnitType unitType = unitTypeRepository.findById(unitRequest.getUnitTypeId()).orElseThrow(() -> new ResourceNotFoundException("Unit type not found with id: " + unitRequest.getUnitTypeId()));
        unit.setUnitType(unitType);

        Unit savedUnit = unitRepository.save(unit);

        // Создаем событие о создании объекта
        // eventService.createEvent(...);

        // Обновляем кэш
        // cacheService.updateCache(...);

        return UnitMapper.MAPPER.mapEntityToResponse(savedUnit);
    }

    @Transactional(readOnly = true)
    public UnitResponse getUnitById(UUID unitId) {
        Unit unit = unitRepository.findById(unitId).orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + unitId));
        return UnitMapper.MAPPER.mapEntityToResponse(unit);
    }

    @Transactional
    public void deleteUnitById(UUID unitId, UUID userId) {
        log.info("Delete unit by id request: {}", unitId);
        //TODO implement
        //Check if user is owner of the unit
    }

    @Transactional
    public UnitResponse updateUnit(UnitRequest unitRequest, UUID userId) {
        log.info("Update unit id: {}", unitRequest.getId());
        //TODO implement
        //Check if user is owner of the unit
        return null;
    }

    @Transactional(readOnly = true)
    public PageableResponse<UnitResponse> searchUnits(UnitSearchRequest searchRequest, Pageable pageable) {
        Page<Unit> units = unitRepository.searchUnits(
                true,
                searchRequest.getStartDate(),
                searchRequest.getEndDate(),
                searchRequest.getBasePriceFrom(),
                searchRequest.getBasePriceTo(),
                searchRequest.getUnitTypeId(),
                searchRequest.getRoomsCount(),
                searchRequest.getFloor(),
                List.of(BookingStatusEnum.PENDING.name(), BookingStatusEnum.CONFIRMED.name()),
                pageable);

        log.debug("Found {} units", units.getTotalElements());
        return mapPageableResponse(UnitMapper.MAPPER::mapEntityToResponse, units);
    }

    private <E, R> PageableResponse<R> mapPageableResponse(Function<E, R> mapper, Page<E> unitPage) {
        List<R> content = unitPage.getContent().stream().map(mapper).toList();

        int pageNumber = unitPage.getNumber();
        int pageSize = unitPage.getSize();
        long totalElements = unitPage.getTotalElements();
        int totalPages = unitPage.getTotalPages();
        boolean lastPage = unitPage.isLast();

        return new PageableResponse<>(content, pageNumber, pageSize, totalElements, totalPages, lastPage);
    }

    @Transactional(readOnly = true)
    public List<UnitTypeResponse> getAllUnitTypes() {
        List<UnitType> unitTypes = unitTypeRepository.findAll();
        return unitTypes.stream().map(UnitTypeMapper.MAPPER::mapEntityToResponse).toList();
    }

    public Map<String, Object> getUnitStatistics() {
        //TODO implement
        return null;
    }
}
