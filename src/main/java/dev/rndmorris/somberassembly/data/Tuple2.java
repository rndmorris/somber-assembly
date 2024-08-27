package dev.rndmorris.somberassembly.data;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record Tuple2<T1, T2> (T1 value1, T2 value2) {}
