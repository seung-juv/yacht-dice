package com.yacht.app.config.domain;

import com.yacht.jpa.domain.BaseRepository;

import java.util.Optional;

public interface ConfigRepository extends BaseRepository<Config, Long> {

    Optional<Config> findTopByOrderByIdDesc();

}
