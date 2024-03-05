package com.yacht.app.user.domain;

import com.yacht.jpa.domain.BaseRepository;

import java.util.Optional;

public interface AccountRepository extends BaseRepository<Account, Long> {

    Optional<Account> findByUsername(String username);
}
