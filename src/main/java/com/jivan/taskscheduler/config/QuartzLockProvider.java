package com.jivan.taskscheduler.config;

import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Optional;

@Configuration
public class QuartzLockProvider implements LockProvider {

    private final JdbcTemplate jdbcTemplate;

    public QuartzLockProvider(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public Optional<SimpleLock> lock(LockConfiguration lockConfiguration) {
        String lockName = lockConfiguration.getName();
        long lockAtMostFor = lockConfiguration.getLockAtMostFor().toMillis();
        long lockAtLeastFor = lockConfiguration.getLockAtLeastFor().toMillis();
        long currentTime = System.currentTimeMillis();
        long lockUntil = currentTime + lockAtLeastFor;

        int updatedRows = jdbcTemplate.update("INSERT INTO shedlock (name, lock_until, locked_at) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE locked_at = ?, lock_until = ?",
                lockName, lockUntil, currentTime, currentTime, lockUntil);
        if (updatedRows == 1) {
            return Optional.of(new SimpleLock() {
                @Override
                public void unlock() {
                    jdbcTemplate.update("DELETE FROM shedlock WHERE name = ?", lockName);
                }
            });
        } else {
            return Optional.empty();
        }
    }


    public void unlock(SimpleLock lock) {
        return;
    }

}

