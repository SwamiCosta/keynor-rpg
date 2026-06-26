package com.keynor.rpg.infrastructure.config;

import com.keynor.rpg.domain.port.out.RandomSource;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class JdkRandomSource implements RandomSource {

    private final Random random = new Random();

    @Override
    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    @Override
    public double nextDouble() {
        return random.nextDouble();
    }
}
