/*******************************************************************************
* Copyright (c) {2009,2011} {Software Design and Collaboration Laboratory (SDCL)
*				, University of California, Irvine}.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    {Software Design and Collaboration Laboratory (SDCL)
*	, University of California, Irvine}
*			- initial API and implementation and/or initial documentation
*******************************************************************************/
package edu.uci.lighthouse.lighthouseqandathreads.view.CompartmentViews;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.uci.lighthouse.lighthouseqandathreads.PersistAndUpdate;
import edu.uci.lighthouse.lighthouseqandathreads.view.LayoutMetrics;
import edu.uci.lighthouse.lighthouseqandathreads.view.WindowFrame;
import edu.uci.lighthouse.model.QAforums.ForumThread;
import edu.uci.lighthouse.model.QAforums.Post;
import edu.uci.lighthouse.model.QAforums.TeamMember;

public class ReplyBoxView extends WindowFrame {
	private TeamMember tm;
	private ForumThread thread;
	private PersistAndUpdate pu;
	GridData compData;
	Composite composite;
	Button postButton;
	final StyledText postNewThreadBox;
	private String message;

	public ReplyBoxView(Composite parent, int style, ForumThread thread,
			TeamMember tm, PersistAndUpdate pu) {
		super(parent, style);
		this.setThread(thread);
		this.tm = tm;
		
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 1;
		layout.verticalSpacing = 1;
		layout.marginWidth = 1;
		layout.marginHeight = 1;
		
		
		this.setLayout(layout);
		this.pu = pu;
		Color postBack = new Color(this.getDisplay(), 255, 212, 102);
		this.setBackground(postBack);
		compData = new GridData(LayoutMetrics.QUESTION_BOX_VIEW_WIDTH,
				LayoutMetrics.QUESTION_BOX_VIEW_HEIGHT);

		composite = new Composite(this, SWT.None);
		composite.setLayout(layout);
		composite.setLayoutData(compData);
		composite.setBackground(ColorConstants.black);

		Color replyBorderColor = new Color(this.getDisplay(), 33, 138, 255);

		composite.setBackground(replyBorderColor);

		PostListener postListener = new PostListener();

		postButton = new Button(this, SWT.BORDER);
		postButton.setText("post");
		postButton.addSelectionListener(postListener);

		this.getElementMenu().addButton(postButton);
		this.getElementMenu().setBackground(postBack);

		GridData postNewThreadBoxData = new GridData(SWT.FILL, SWT.FILL, true,
				true);
		postNewThreadBox = new StyledText(composite, SWT.BORDER | SWT.V_SCROLL);

		postNewThreadBox.setLayoutData(postNewThreadBoxData);

		postNewThreadBox.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setMessage(postNewThreadBox.getText());

			}
		});

		postNewThreadBox.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// change size of postNewThreadBox here?

			}

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

		});

	}

	private class PostListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {

			Post newPost = new Post(true, "", message, tm);
			getThread().getRootQuestion().addResponse(newPost);
			postNewThreadBox.setText("");
			pu.run();
			ReplyBoxView.this.getShell().close();

		}
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setThread(ForumThread thread) {
		this.thread = thread;
	}

	public ForumThread getThread() {
		return thread;
	}
}
