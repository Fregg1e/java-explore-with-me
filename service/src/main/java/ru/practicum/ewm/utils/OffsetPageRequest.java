package ru.practicum.ewm.utils;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Objects;

public class OffsetPageRequest implements Pageable {
    private final int limit;
    private final int offset;
    private final Sort sort;

    public OffsetPageRequest(int offset, int limit, Sort sort) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset не должен быть меньше 0!");
        }

        if (limit < 1) {
            throw new IllegalArgumentException("Limit не должен быть меньше 1!");
        }
        this.limit = limit;
        this.offset = offset;
        this.sort = sort;
    }

    public OffsetPageRequest(int offset, int limit) {
        this(offset, limit, Sort.unsorted());
    }

    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OffsetPageRequest((int) getOffset() + getPageSize(), getPageSize());
    }

    public OffsetPageRequest previous() {
        return hasPrevious() ? new OffsetPageRequest((int) getOffset() - getPageSize(), getPageSize()) : this;
    }


    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public Pageable first() {
        return new OffsetPageRequest(0, getPageSize());
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new OffsetPageRequest(pageNumber, getPageSize());
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OffsetPageRequest that = (OffsetPageRequest) o;
        return limit == that.limit && offset == that.offset && Objects.equals(sort, that.sort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(limit, offset, sort);
    }

    @Override
    public String toString() {
        return "OffsetPageRequest{" +
                "limit=" + limit +
                ", offset=" + offset +
                ", sort=" + sort +
                '}';
    }
}
