package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.model.Location;

public class LocationMapper {
    public static LocationDto fromLocationToLocationDto(Location location) {
        return LocationDto.builder()
                .lon(location.getLon())
                .lat(location.getLat())
                .build();
    }

    public static Location fromLocationDtoToLocation(LocationDto locationDto) {
        return Location.builder()
                .lon(locationDto.getLon())
                .lat(locationDto.getLat())
                .build();
    }
}
