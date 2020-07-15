package cn.nihility.boot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Foo
 *
 * @author dandelion
 * @date 2020-04-12 11:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Foo implements Serializable {
    private static final long serialVersionUID = -697940505789397373L;

    private long id;
    private String name;
    private String info;

    public Foo(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
