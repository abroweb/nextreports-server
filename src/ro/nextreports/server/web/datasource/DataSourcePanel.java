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
package ro.nextreports.server.web.datasource;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.nextreports.server.domain.DataSource;
import ro.nextreports.server.domain.DriverTemplate;
import ro.nextreports.server.service.DataSourceService;
import ro.nextreports.server.service.StorageService;
import ro.nextreports.server.util.StorageUtil;
import ro.nextreports.server.web.common.form.AdvancedForm;
import ro.nextreports.server.web.common.panel.NextFeedbackPanel;
import ro.nextreports.server.web.core.BasePage;
import ro.nextreports.server.web.core.EntityBrowserPanel;
import ro.nextreports.server.web.core.validation.DuplicationEntityValidator;
import ro.nextreports.server.web.core.validation.JcrNameValidator;

import ro.nextreports.engine.querybuilder.sql.dialect.CSVDialect;

/**
 * @author Decebal Suiu
 */
public class DataSourcePanel extends Panel {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourcePanel.class);

    private DataSource dataSource;
    private String parentPath;
    private boolean modify;

    @SpringBean
    private DataSourceService dataSourceService;

    @SpringBean
    private StorageService storageService;

    public DataSourcePanel(String id, final String parentPath) {
        this(id, parentPath, new DataSource());
    }

    public DataSourcePanel(String id, final String parentPath, DataSource dataSource) {
        super(id);

        this.parentPath = parentPath;

        this.dataSource = dataSource;
        if (dataSource.getName() != null) {
            this.modify = true;
        }

        AdvancedForm<DataSource> form = new DataSourceForm("form");

        NextFeedbackPanel feedbackPanel = new NextFeedbackPanel("feedback", form);
        feedbackPanel.setOutputMarkupId(true);
        feedbackPanel.setEscapeModelStrings(false);
        form.add(feedbackPanel);

        add(form);        

        setOutputMarkupId(true);
    }

    private class DataSourceForm extends AdvancedForm<DataSource> {

        private DriverTemplate template;
        private String testResponse;

        public DataSourceForm(String id) {
            super(id, new CompoundPropertyModel<DataSource>(dataSource));
            setOutputMarkupId(true);

            String title = getString("ActionContributor.DataSource.create");
            if (modify) {
                title = getString("ActionContributor.DataSource.modify");
            }
            add(new Label("title", title));

            final TextField<String> name = new TextField<String>("name")  {
            	
                @Override
				public boolean isEnabled() {
					return !modify;
				}
                
            };
            name.add(new JcrNameValidator());
            name.setRequired(true);
            name.setLabel(new Model<String>(getString("ActionContributor.DataSource.name")));
            add(name);

            if (!modify) {
                add(new DuplicationEntityValidator(name, parentPath));
            }

            final TextField<String> driver = new TextField<String>("driver");
            driver.setRequired(true);
            driver.setLabel(new Model<String>(getString("ActionContributor.DataSource.driver")));
            driver.setOutputMarkupId(true);
            add(driver);
            final TextField<String> url = new TextField<String>("url");
            url.setRequired(true);
            url.setLabel(new Model<String>(getString("ActionContributor.DataSource.url")));
            url.setOutputMarkupId(true);
            add(url);

            List<DriverTemplate> driverTemplates;
            try {
                driverTemplates = dataSourceService.getDriverTemplates();
            } catch (Exception e) {
                driverTemplates = Collections.emptyList();
                e.printStackTrace();
                LOG.error(e.getMessage(), e);
                error(e.getMessage());
            }
            if (!modify && !driverTemplates.isEmpty()) {
                template = driverTemplates.get(0);
                dataSource.setDriver(template.getClassName());
                dataSource.setVendor(template.getType());
                dataSource.setUrl(template.getUrlTemplate());
            }
            if (modify) {
                template = getDriverTemplate(driverTemplates, dataSource.getVendor());
                System.out.println("modify=" + template);
            }
            ChoiceRenderer<DriverTemplate> typeRenderer = new ChoiceRenderer<DriverTemplate>("type");
            final DropDownChoice<DriverTemplate> templates = new DropDownChoice<DriverTemplate>("type",
                    new PropertyModel<DriverTemplate>(this, "template"), driverTemplates, typeRenderer);
//		templates.add(new AjaxIndicatorAppender());
            templates.add(new AjaxFormComponentUpdatingBehavior("onchange") {

                protected void onUpdate(AjaxRequestTarget target) {
                    dataSource.setDriver(template.getClassName());
                    dataSource.setVendor(template.getType());
                    dataSource.setUrl(template.getUrlTemplate());
                    target.add(driver);
                    target.add(url);
                }

            });
            add(templates);

            final TextField<String> username = new TextField<String>("username");
            add(username);
            final PasswordTextField password = new PasswordTextField("password");
            password.setRequired(false);
            password.setResetPassword(false);
            add(password);

            add(new AjaxLink("cancel") {

                @Override
                public void onClick(AjaxRequestTarget target) {
                    back(target);
                }
                
            });

            AjaxButton testButton = new AjaxButton("test", this) {
            	
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    try {
                        testResponse = dataSourceService.testConnection(dataSource);
                        testResponse = testResponse.replaceAll("\n", "<br>");
                        form.info(testResponse);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        form.error(e.getMessage());
                    }
                    target.add(form);
                }

                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.add(form);
                }
                
            };            
            add(testButton);
            
            add(new AjaxButton("create", this) {
            	
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    try {
                        if (modify) {
                            storageService.modifyEntity(dataSource);
                        } else {
                            dataSource.setPath(StorageUtil.createPath(parentPath, dataSource.getName()));
                            storageService.addEntity(dataSource);
                        }
                        back(target);
                    } catch (Exception e) {
                        e.printStackTrace();
                        form.error(e.getMessage());
                        target.add(form);
                    }
                }

                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.add(form);
                }
                
            });
            
            AjaxButton propButton = new AjaxButton("properties", this) {
            	
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    try {
                    	ModalWindow dialog = findParent(BasePage.class).getDialog();
        	            dialog.setTitle(getString("properties"));
        	            dialog.setInitialWidth(430);
        	            dialog.setUseInitialHeight(false);
        	            dialog.setContent(new DataSourcePropertiesPanel(dialog.getContentId(), Model.of(dataSource)));
        	            dialog.show(target);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        form.error(e.getMessage());
                    }
                    target.add(form);
                }

                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.add(form);
                }

				@Override
				public boolean isVisible() {					
					return dataSource.getDriver().equals(CSVDialect.DRIVER_CLASS);
				}                                
                
            };            
            add(propButton);
        }
    }

    private DriverTemplate getDriverTemplate(List<DriverTemplate> templates, String type) {
        for (DriverTemplate template : templates) {
            if (template.getType().equals(type)) {
                return template;
            }
        }
        
        return null;
    }

    private void back(AjaxRequestTarget target) {
        EntityBrowserPanel panel = findParent(EntityBrowserPanel.class);
        panel.backwardWorkspace(target);
    }

}
