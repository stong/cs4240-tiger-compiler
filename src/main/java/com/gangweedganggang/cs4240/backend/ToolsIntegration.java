package com.gangweedganggang.cs4240.backend;

import java.io.File;

public interface ToolsIntegration {

    default String executeAssemblyFile(File file) {
        return null;
    }

}
