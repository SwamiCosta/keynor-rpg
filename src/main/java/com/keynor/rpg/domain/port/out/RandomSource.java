package com.keynor.rpg.domain.port.out;

public interface RandomSource {

    int nextInt(int bound);

    double nextDouble();
}
