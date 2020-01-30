package ru.innopolis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.innopolis.domain.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}