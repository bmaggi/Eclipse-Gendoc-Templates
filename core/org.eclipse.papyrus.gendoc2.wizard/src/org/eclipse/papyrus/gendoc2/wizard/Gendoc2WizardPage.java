/*****************************************************************************
 * Copyright (c) 2011 Atos Origin.
 *
 *    
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Barthelemy HABA (Atos Origin) barthelemy.haba@atosorigin.com - 
 *
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.wizard;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.ui.dialogs.WorkspaceResourceDialog;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class Gendoc2WizardPage extends WizardPage
{
    private Text text;

    private Text text_1;

    private Text text_2;

    private Combo combo_1;

    private ComboViewer comboViewer_1;

    private Combo combo;

    private ComboViewer comboViewer;

    private final int OFFSET = 3;

    /**
     * Create the wizard.
     */
    public Gendoc2WizardPage()
    {
        super("wizardPage");
        setTitle("Documentation Generation (Default Generation)");

    }

    /**
     * Create contents of the wizard.
     * 
     * @param parent represent the composite passed automatically to this method by the system
     */
    public void createControl(Composite parent)
    {
        Composite container = new Composite(parent, SWT.NONE);

        setControl(container);
        container.setLayout(new GridLayout(3, false));

        if (getGWizard().getRunners().size() > 1)
        {
            Label lblNewLabel_4 = new Label(container, SWT.NONE);
            GridData gd_lblNewLabel_4 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
            gd_lblNewLabel_4.widthHint = 89;
            lblNewLabel_4.setLayoutData(gd_lblNewLabel_4);
            lblNewLabel_4.setText(" List of templates:");
            comboViewer_1 = new ComboViewer(container, SWT.NONE);
            combo_1 = comboViewer_1.getCombo();
            GridData gd_combo_1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
            gd_combo_1.widthHint = 475;
            combo_1.setLayoutData(gd_combo_1);
            combo_1.select(0);
            manageTemplateCombo(comboViewer_1);

        }

        Label lblNewLabel = new Label(container, SWT.NONE);
        lblNewLabel.setText("Selected File:");

        text = new Text(container, SWT.BORDER);
        text.setEnabled(false);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        Label lblNewLabel_1 = new Label(container, SWT.NONE);
        lblNewLabel_1.setText("Output Format:");

        comboViewer = new ComboViewer(container, SWT.NONE);
        combo = comboViewer.getCombo();
        combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        Label lblNewLabel_2 = new Label(container, SWT.NONE);
        lblNewLabel_2.setText("Output Path:");
        manageCombo(comboViewer);
        text_1 = new Text(container, SWT.BORDER);
        text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Button btnNewButton = new Button(container, SWT.NONE);
        btnNewButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        btnNewButton.setText("browse...");

        Label lblNewLabel_3 = new Label(container, SWT.NONE);
        lblNewLabel_3.setText("File Name:");

        text_2 = new Text(container, SWT.BORDER);
        text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        IFile fileName = getGWizard().getInputFile(getGWizard().getRunners().get(0));
        StringTokenizer token = new StringTokenizer(fileName.getName(), ".");
        String nomFile = token.nextToken();
        text.setText(fileName.getName());
        String directory = getUriText(URI.createURI(fileName.getLocationURI().toString()));
        directory = replacePercentBySpace(directory, OFFSET);
        text_1.setText(directory);
        text_2.setText(nomFile + "_doc");

        manageListeners(comboViewer, btnNewButton);

        Group grpSaveAction = new Group(container, SWT.NONE);
        grpSaveAction.setText("Save Action");
        grpSaveAction.setLayout(new FormLayout());
        GridData gd_grpSaveAction = new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1);
        gd_grpSaveAction.widthHint = 567;
        grpSaveAction.setLayoutData(gd_grpSaveAction);

        Button btnNewButton_1 = new Button(grpSaveAction, SWT.NONE);
        FormData fd_btnNewButton_1 = new FormData();
        fd_btnNewButton_1.left = new FormAttachment(100, -65);
        fd_btnNewButton_1.bottom = new FormAttachment(100, -3);
        fd_btnNewButton_1.right = new FormAttachment(100);
        btnNewButton_1.setLayoutData(fd_btnNewButton_1);
        btnNewButton_1.setText("Save");
        this.setDescription(this.getSelected().getDescription());
        btnNewButton_1.addSelectionListener(new SelectionListener()
        {

            public void widgetSelected(SelectionEvent e)
            {
                if (getSelected() != null)
                {
                    try
                    {
                        IContainer[] result = WorkspaceResourceDialog.openFolderSelection(getShell(), "Folder Selection", "Select an output folder", false, new Object[] {}, null);
                        if (result != null && result.length == 1)
                        {
                            IContainer folder = result[0];
                            IFile file = folder.getFile(new Path("generic_template." + getSelected().getOutPutExtension()));
                            InputStream stream = getSelected().getTemplate().openStream();
                            try
                            {
                                file.create(stream, true, new NullProgressMonitor());
                                MessageDialog.openInformation(getShell(), "Information", "the generic template has been saved at :\n" + file.getFullPath().toString());
                            }
                            catch (CoreException e1)
                            {
                                MessageDialog.openError(getShell(), "Error", e1.getLocalizedMessage());
                            }
                        }

                    }
                    catch (IOException e1)
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                else
                {
                    setErrorMessage("Please select an output extension");
                }
            }

            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
        });

        Label lblYouCanSave = new Label(grpSaveAction, SWT.NONE);
        FormData fd_lblYouCanSave = new FormData();
        fd_lblYouCanSave.top = new FormAttachment(0, 5);
        fd_lblYouCanSave.bottom = new FormAttachment(100, -7);
        fd_lblYouCanSave.left = new FormAttachment(0, 10);
        lblYouCanSave.setLayoutData(fd_lblYouCanSave);
        lblYouCanSave.setText("You can save the Gendoc2 template used for the generation to modify it :");

        if (getGWizard().getRunners().size() > 1)
        {
            comboViewer_1.addSelectionChangedListener(new ISelectionChangedListener()
            {
                public void selectionChanged(SelectionChangedEvent event)
                {
                    IGendoc2Runner runners = Gendoc2WizardPage.this.getSelectedRunner();
                    Gendoc2WizardPage.this.comboViewer.setInput(runners);
                    Gendoc2WizardPage.this.combo.select(0);
                    Gendoc2WizardPage.this.setDescription(Gendoc2WizardPage.this.getSelected().getDescription());
                    getGWizard().refresh();
                }
            });
        }
    }

    /**
     * this method put all the necessary listeners to the comboviewer, btnnewButton, text_1, text_2.
     * 
     * @param comboViewer the combo viewer used to output all kind of document to generate
     * @param btnNewButton the btn new button that represent the browser on the wizard *
     */
    private void manageListeners(ComboViewer comboViewer, Button btnNewButton)
    {
        btnNewButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                // TODO Auto-generated method stub
                IContainer[] result = WorkspaceResourceDialog.openFolderSelection(getShell(), "Folder Selection", "Select an output folder", false, new Object[] {}, null);
                if (result != null && result.length == 1)
                {
                    IContainer folder = result[0];
                    String repertoire = URI.createURI(folder.getLocationURI().toString()).devicePath().toString();
                    repertoire = replacePercentBySpace(repertoire, OFFSET);
                    text_1.setText(repertoire + "/");
                    getGWizard().refresh();
                }
            }
        });

        text_1.addModifyListener(new ModifyListener()
        {

            public void modifyText(ModifyEvent e)
            {
                getGWizard().refresh();
            }

        });
        text_2.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                // TODO Auto-generated method stub

                getGWizard().refresh();
            }
        });
        comboViewer.addSelectionChangedListener(new ISelectionChangedListener()
        {
            public void selectionChanged(SelectionChangedEvent event)
            {
                Gendoc2WizardPage.this.setDescription(Gendoc2WizardPage.this.getSelected().getDescription());
                getGWizard().refresh();
            }
        });

    }

    /**
     * this method add all the necessary listeners to the comboviewer1
     * 
     * @param comboViewer1 the combo viewer1 that output all the possible template of model
     */
    private void manageTemplateCombo(ComboViewer comboViewer1)
    {
        comboViewer1.setContentProvider(new IStructuredContentProvider()
        {

            public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
            {
            }

            public void dispose()
            {
            }

            public Object[] getElements(Object inputElement)
            {
                if (inputElement instanceof Collection)
                {
                    Collection coll = (Collection) inputElement;
                    return coll.toArray();
                }
                return null;
            }
        });

        comboViewer1.setLabelProvider(new ILabelProvider()
        {

            public void removeListener(ILabelProviderListener listener)
            {
            }

            public boolean isLabelProperty(Object element, String property)
            {
                return true;
            }

            public void dispose()
            {
            }

            public void addListener(ILabelProviderListener listener)
            {
            }

            public Image getImage(Object element)
            {
                return null;
            }

            public String getText(Object element)
            {
                if (element instanceof IGendoc2Runner)
                {
                    return ((IGendoc2Runner) element).getLabel();
                }
                else
                {
                    return null;
                }

            }
        });

        comboViewer1.setInput(getGWizard().getRunners());
        combo_1.addModifyListener(new ModifyListener()
        {

            public void modifyText(ModifyEvent e)
            {
                IGendoc2Runner selectedRunner = getSelectedRunner();

                if (comboViewer != null && selectedRunner != null)
                {
                    comboViewer.setInput(selectedRunner);

                    if (combo.getItemCount() > 0)
                    {
                        combo.select(0);
                    }
                }
                getGWizard().refresh();
            }
        });
        if (combo_1.getItemCount() > 0)
        {
            combo_1.select(0);

        }
    }

    /**
     * this method make all initialization of content of the comboviewer
     * 
     * @param comboViewer
     */
    private void manageCombo(ComboViewer comboViewer)
    {
        comboViewer.setContentProvider(new IStructuredContentProvider()
        {

            public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
            {
            }

            public void dispose()
            {
            }

            public Object[] getElements(Object inputElement)
            {
                if (inputElement instanceof IGendoc2Runner)
                {
                    IGendoc2Runner runner = (IGendoc2Runner) inputElement;
                    return runner.getGendoc2Templates().toArray();
                }
                return null;
            }
        });

        comboViewer.setLabelProvider(new ILabelProvider()
        {

            public void removeListener(ILabelProviderListener listener)
            {
            }

            public boolean isLabelProperty(Object element, String property)
            {
                return true;
            }

            public void dispose()
            {
            }

            public void addListener(ILabelProviderListener listener)
            {
            }

            public Image getImage(Object element)
            {
                return null;
            }

            public String getText(Object element)
            {
                if (element instanceof IGendoc2Template)
                {
                    return ((IGendoc2Template) element).getOutPutExtension();
                }
                else
                {
                    return null;
                }

            }
        });

        IGendoc2Runner selectedRunner = getSelectedRunner();

        if (selectedRunner != null)
        {
            comboViewer.setInput(getSelectedRunner());

        }
        combo.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                getGWizard().refresh();
            }
        });
        if (combo.getItemCount() > 0)
        {
            combo.select(0);
        }
    }

    /**
     * this method give the good path in String associated to an URI
     * 
     * @param uri the uri contains the path to access to the file selected
     * @return the uri text contains the adequate access path in String
     */
    public String getUriText(URI uri)
    {
        return uri.trimSegments(1).devicePath().toString() + "/";
    }

    /**
     * @return the wizard in witch this page is added
     */
    public Gendoc2Wizard getGWizard()
    {
        return (Gendoc2Wizard) getWizard();
    }

    /**
     * @return the output path in string contained in the component text_1
     */
    public String getOutputPath()
    {
        return text_1.getText();
    }

    /**
     * verify if the value in the displayed in the comboviewer is a correct extension.
     * 
     * @return true, if is correct extension
     */
    public boolean isCorrectExtension()
    {
        return (Arrays.asList(combo.getItems()).contains(combo.getText()));
    }

    /**
     * this method verify that all component is filled or get a value
     * 
     * @return true, if successful
     */
    public boolean allIsFilled()
    {
        if (combo_1 != null)
        {
            return ((text_1.getText().length() > 0) && (text_2.getText().length() > 0) && existeTemplate() && (combo.getText().length() > 0));
        }
        else
        {
            return ((text_1.getText().length() > 0) && (text_2.getText().length() > 0) && (combo.getText().length() > 0));
        }
    }

    /**
     * this method verify if the comboviewer1 is not empty or get at least one template
     * 
     * @return true, if successful
     */
    public boolean existeTemplate()
    {
        return (combo_1.getText().length() > 0);
    }

    /**
     * @return the selected runner of the template selected at this time in the comboviewer1
     */
    public IGendoc2Runner getSelectedRunner()
    {
        if (getGWizard().getRunners().size() == 1)
        {
            return getGWizard().getRunners().get(0);
        }
        else
        {
            if (comboViewer_1.getSelection() instanceof IStructuredSelection)
            {
                IStructuredSelection select = (IStructuredSelection) comboViewer_1.getSelection();
                return ((IGendoc2Runner) select.getFirstElement());
            }
        }
        return null;
    }

    /**
     * @return template associated to the format of document to generate, that is selected at this time in the
     *         comboviewer
     */
    public IGendoc2Template getSelected()
    {
        if (comboViewer.getSelection() instanceof IStructuredSelection)
        {
            IStructuredSelection select = (IStructuredSelection) comboViewer.getSelection();
            return ((IGendoc2Template) select.getFirstElement());
        }

        return null;
    }

    /**
     * @return the full output path. It represent the good path of where to put document to generate
     */
    public String getFullOutputPath()
    {
        return text_1.getText() + text_2.getText() + "." + combo.getText();
    }

    /**
     * @param theString string containing percentage caracteres
     * @param offset the number of characters that represent space in the system
     * @return theString with any percentage caracter
     */
    public String replacePercentBySpace(String theString, int offset)
    {
        StringBuffer buffer = new StringBuffer(theString);
        int pos = 1;
        while (theString.contains("%"))
        {
            pos = theString.indexOf("%"); // Position of first occurence
            theString = buffer.replace(pos, pos + offset, " ").toString();
        }
        return theString;
    }

    /**
     * @return the path of the repository of the model selected by the user.
     */
    public String getModel()
    {
        return URI.createURI(getGWizard().getInputFile(getSelectedRunner()).getLocationURI().toString()).devicePath();
    }
}
