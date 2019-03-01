package everthing.core.common;

import lombok.Data;

import java.util.Set;

/**
 * 所有路径
 */
@Data
public class HandPath {
    private Set<String> includePath;
    private Set<String> excludePsth;
}
