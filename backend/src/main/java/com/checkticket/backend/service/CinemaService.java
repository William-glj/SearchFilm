package com.checkticket.backend.service;

import com.checkticket.backend.dto.CinemaDto;
import com.checkticket.backend.entity.Cinema;
import com.checkticket.backend.repository.CinemaRepository;
import com.checkticket.backend.utils.Mappers;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.StreamSupport;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CinemaService {

    private final CinemaRepository cinemaRepository;

    public CinemaService(CinemaRepository cinemaRepository) {
        this.cinemaRepository = cinemaRepository;
    }

    @Transactional(readOnly = true)
    public List<CinemaDto> listAll() {
        return StreamSupport.stream(cinemaRepository.findAll().spliterator(), false)
                .map(Mappers::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CinemaDto getById(Long id) {
        return Mappers.toDto(cinemaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cinema not found")));
    }

    @Transactional
    public CinemaDto create(CinemaDto dto) {
        if (dto.externalOsmId() != null && !dto.externalOsmId().isBlank()
                && cinemaRepository.findByExternalOsmId(dto.externalOsmId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cinema already exists");
        }
        Cinema cinema = new Cinema();
        String osmId = (dto.externalOsmId() == null || dto.externalOsmId().isBlank())
                ? "manual-" + UUID.randomUUID()
                : dto.externalOsmId();
        cinema.setExternalOsmId(osmId);
        cinema.setName(dto.name());
        cinema.setCity(dto.city());
        cinema.setZone(dto.zone());
        cinema.setLatitude(dto.latitude());
        cinema.setLongitude(dto.longitude());
        return Mappers.toDto(cinemaRepository.save(cinema));
    }

    /** Sincroniza cines desde Overpass API (best-effort, requiere conexión a internet). */
    @Transactional
    public int syncFromOverpass(String city) {
        if (city == null || city.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "city is required");
        }
        String query = String.format(
                "[out:json];area[\"name\"=\"%s\"]->.a;(node[\"amenity\"=\"cinema\"](area.a););out center;",
                city);
        String body = "data=" + URLEncoder.encode(query, StandardCharsets.UTF_8);

        Map<String, Object> root = RestClient.create()
                .post()
                .uri("https://overpass-api.de/api/interpreter")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        int synced = 0;
        if (root == null || !(root.get("elements") instanceof List<?> elements)) {
            return 0;
        }
        for (Object elementObj : elements) {
            if (!(elementObj instanceof Map<?, ?> element)) {
                continue;
            }
            String type = String.valueOf(element.get("type"));
            long id = element.get("id") instanceof Number n ? n.longValue() : 0L;
            String osmId = type + "/" + id;
            if (cinemaRepository.findByExternalOsmId(osmId).isPresent()) {
                continue;
            }

            String name = "Cinema";
            if (element.get("tags") instanceof Map<?, ?> tags && tags.get("name") != null) {
                name = String.valueOf(tags.get("name"));
            }
            double latitude = element.get("lat") instanceof Number n ? n.doubleValue() : 0.0;
            double longitude = element.get("lon") instanceof Number n ? n.doubleValue() : 0.0;

            Cinema cinema = new Cinema();
            cinema.setExternalOsmId(osmId);
            cinema.setName(name);
            cinema.setCity(city);
            cinema.setLatitude(latitude);
            cinema.setLongitude(longitude);
            cinemaRepository.save(cinema);
            synced++;
        }
        return synced;
    }
}
