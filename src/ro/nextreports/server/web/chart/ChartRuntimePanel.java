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
package ro.nextreports.server.web.chart;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.RangeValidator;
import org.springframework.beans.factory.annotation.Required;

import ro.nextreports.server.domain.Chart;
import ro.nextreports.server.domain.DataSource;
import ro.nextreports.server.report.next.NextUtil;
import ro.nextreports.server.service.StorageService;
import ro.nextreports.server.util.ChartUtil;
import ro.nextreports.server.web.dashboard.WidgetRuntimeModel;
import ro.nextreports.server.web.report.DynamicParameterRuntimePanel;

import ro.nextreports.engine.Report;

/**
 * User: mihai.panaitescu
 * Date: 01-Feb-2010
 * Time: 15:08:21
 */
public class ChartRuntimePanel extends DynamicParameterRuntimePanel {

    private Chart chart;
    
    @SpringBean 
    StorageService storageService;

    public ChartRuntimePanel(String id, final Chart chart, WidgetRuntimeModel runtimeModel) {
        super(id, false);
        this.chart = chart;
        if (runtimeModel.getChartType() == null) {
            runtimeModel.setChartType(ChartUtil.CHART_LINE);
        }
        init(runtimeModel);
    }

    @SuppressWarnings("unchecked")
    public void addWicketComponents() {
        DropDownChoice exportChoice = new DropDownChoice("chartType", new PropertyModel(runtimeModel, "chartType"), ChartUtil.CHART_TYPES);
        exportChoice.setRequired(true);
        add(exportChoice);

        TextField<Integer> refreshText = new TextField<Integer>("refreshTime", new PropertyModel(runtimeModel, "refreshTime"));
        refreshText.add(new RangeValidator<Integer>(0, 3600));
        refreshText.setRequired(true);
        add(refreshText);
        
        TextField<Integer> timeoutText = new TextField<Integer>("timeout", new PropertyModel(runtimeModel, "timeout"));
        timeoutText.add(new RangeValidator<Integer>(5, 600));
        timeoutText.setLabel(new Model<String>("Timeout"));
        timeoutText.setRequired(true);
        add(timeoutText);
    }    

    public Report getNextReport() {
        return NextUtil.getNextReport(storageService.getSettings(), chart);
    }

    public DataSource getDataSource() {
        return chart.getDataSource();
    }

    @Required
	public void setStorageService(StorageService storageService) {
		this.storageService = storageService;
	}
    
    


}
