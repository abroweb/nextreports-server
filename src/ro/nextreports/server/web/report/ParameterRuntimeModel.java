/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ro.nextreports.server.web.report;


import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;

import ro.nextreports.server.domain.ReportRuntimeParameterModel;

/**
 * User: mihai.panaitescu
 * Date: 02-Feb-2010
 * Time: 11:38:28
 */
public class ParameterRuntimeModel implements Serializable {

    private HashMap<String, ReportRuntimeParameterModel> runtimeParameterModels;
    private boolean edit;

    public ParameterRuntimeModel() {
        edit = false;
        runtimeParameterModels = new LinkedHashMap<String, ReportRuntimeParameterModel>();
    }
   
    public HashMap<String, ReportRuntimeParameterModel> getParameters() {
        return runtimeParameterModels;
    }

    public void setParameters(HashMap<String, ReportRuntimeParameterModel> runtimeParameterModels) {
        this.runtimeParameterModels = runtimeParameterModels;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

}
