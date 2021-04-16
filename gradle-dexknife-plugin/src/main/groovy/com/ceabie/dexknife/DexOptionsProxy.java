/*
 * Copyright (C) 2016 ceabie (https://github.com/ceabie/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ceabie.dexknife;

import com.android.builder.core.DexOptions;

import java.util.List;

/**
 * The type Dex options proxy.
 *
 * @author ceabie
 */
public class DexOptionsProxy implements DexOptions {
    private DexOptions mDexOptions;
    private List<String> mAddParameters;

    DexOptionsProxy(DexOptions dexOptions, List<String> addParameters) {
        this.mAddParameters = addParameters;
        this.mDexOptions = dexOptions;
    }

    @Override
    public boolean getPreDexLibraries() {
        return mDexOptions.getPreDexLibraries();
    }

    @Override
    public boolean getJumboMode() {
        return mDexOptions.getJumboMode();
    }

    @Override
    public boolean getDexInProcess() {
        return mDexOptions.getDexInProcess();
    }

    @Override
    public String getJavaMaxHeapSize() {
        return mDexOptions.getJavaMaxHeapSize();
    }

    @Override
    public Integer getThreadCount() {
        return mDexOptions.getThreadCount();
    }

    @Override
    public Integer getMaxProcessCount() {
        return mDexOptions.getMaxProcessCount();
    }

    @Override
    public List<String> getAdditionalParameters() {
        return mAddParameters;
    }

    @Override
    public Boolean getOptimize() {
        return mDexOptions.getOptimize();
    }
}
