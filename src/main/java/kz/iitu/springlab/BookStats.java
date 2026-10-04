package kz.iitu.springlab.catalog;

public record BookStats(
        long count,
        Integer earliestYear,
        Integer latestYear
) {
}
