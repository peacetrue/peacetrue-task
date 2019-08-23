package com.github.peacetrue.id;

/**
 * id generator
 *
 * @author xiayx
 */
public interface IdGenerator<Id> {

    /** generate a unique id */
    Id generateId();

}
