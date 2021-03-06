package ru.innopolis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.innopolis.domain.Family;

@Repository
public interface FamilyRepository extends JpaRepository<Family, Long> {
    Family findFirstByName(String name);
}