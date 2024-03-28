package com.fuzzy.subsystems.list;

import java.util.List;

public record ListResult<T>(List<ListItem<T>> items, int matchCount, int nextCount) {

}
