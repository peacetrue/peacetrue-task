package com.github.peacetrue.task.executor;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author xiayx
 */
@Getter
@Setter
public class IdTaskImpl<T> extends TaskImpl {

    private T id;

    public IdTaskImpl() {
    }

    public IdTaskImpl(T id, String body, String input) {
        super(body, input);
        this.id = id;
    }

    public IdTaskImpl(T id, String body, String input, TaskImpl... dependent) {
        super(body, input, dependent);
        this.id = id;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof IdTaskImpl)) return false;
        IdTaskImpl other = (IdTaskImpl) object;
        return other == this || Objects.equals(getId(), other.getId());
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
