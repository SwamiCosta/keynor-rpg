package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PoolAttributeTest {

    @Test
    void atFull_setsCurrentEqualToTotal() {
        PoolAttribute pool = PoolAttribute.atFull(60.0);

        assertThat(pool.total()).isEqualTo(60.0);
        assertThat(pool.current()).isEqualTo(60.0);
    }
}
