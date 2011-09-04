package edu.uci.lighthouse.lighthouseqandathreads.view.CompartmentViews;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Panel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.uci.lighthouse.core.controller.Controller;
import edu.uci.lighthouse.core.data.ISubscriber;
import edu.uci.lighthouse.lighthouseqandathreads.ForumController;
import edu.uci.lighthouse.lighthouseqandathreads.PersistAndUpdate;
import edu.uci.lighthouse.lighthouseqandathreads.PostController;
import edu.uci.lighthouse.lighthouseqandathreads.view.ForumElement;
import edu.uci.lighthouse.lighthouseqandathreads.view.ForumView;
import edu.uci.lighthouse.lighthouseqandathreads.view.LayoutMetrics;
import edu.uci.lighthouse.lighthouseqandathreads.view.ListComposite;
import edu.uci.lighthouse.lighthouseqandathreads.view.RespondBoxView;

import edu.uci.lighthouse.model.LighthouseEvent;
import edu.uci.lighthouse.model.QAforums.ForumThread;
import edu.uci.lighthouse.model.QAforums.Post;
import edu.uci.lighthouse.model.QAforums.TeamMember;

public class CompartmentThreadView extends Composite implements ISubscriber{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6013962858601965104L;
	
	final StyledText postNewThreadBox;
	private String reply = "";
	private Composite replyComposite;
	private ForumThread thread;
	private TeamMember tm;
	private PersistAndUpdate pu;
	private ListComposite listOfReplies;
	private ScrolledComposite scroller;
	
	public CompartmentThreadView(Composite parent, int style, ForumThread thread, TeamMember tm, PersistAndUpdate pu) {
		super(parent, style);

		this.setLayout(new GridLayout(1, false));
		this.setBackground(ColorConstants.blue);
		this.thread = thread;
		this.tm = tm;
		this.pu = pu;
		
		
		scroller = new ScrolledComposite(this,SWT.V_SCROLL | SWT.H_SCROLL);
		GridData scrollData = new GridData(LayoutMetrics.CONVERSATION_LIST_WIDTH, LayoutMetrics.CONVERSATION_LIST_HEIGHT);
		scrollData.horizontalAlignment = GridData.CENTER;
		scroller.setLayoutData(scrollData);
		scroller.setLayout(new GridLayout(1, false));
		
		

		
		listOfReplies = new ListComposite(scroller,SWT.None);
		scroller.setContent(listOfReplies);
		scroller.setMinSize(listOfReplies.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		//---add posts
		for(Post post : thread.getRootQuestion().getResponses()){
			
			
			ForumElement composite = new ForumElement(this, SWT.None);
			GridData data = new GridData(LayoutMetrics.POST_VIEW_WIDTH,
				LayoutMetrics.POST_VIEW_HEIGHT);
			composite.setLayoutData(data);
			composite.setLayout(new GridLayout(1,false));
			Label replyLabel = new Label(composite, SWT.None);
			replyLabel.setText(post.getMessage());
			
			//PostController controller = new PostController(post, composite, pu);
			
			listOfReplies.add(composite);
			listOfReplies.setSize(listOfReplies.computeSize(SWT.DEFAULT, SWT.DEFAULT));

			listOfReplies.renderList();
		}
		

		//-- reply box
		
		GridData compsiteData = new GridData(
				LayoutMetrics.RESPOND_BOX_VIEW_WIDTH,
				LayoutMetrics.RESPOND_BOX_VIEW_HEIGHT);
		replyComposite = new Composite(this, style);
		replyComposite.setLayout(new GridLayout(2, false));
		replyComposite.setLayoutData(compsiteData);
		Color replyBorderColor = new Color(this.getDisplay(), 33, 138, 255);
		replyComposite.setBackground(replyBorderColor);

		postNewThreadBox = new StyledText(replyComposite, SWT.BORDER | SWT.V_SCROLL);
		GridData postNewThreadBoxData = new GridData(SWT.FILL, SWT.FILL, true,
				true);
		postNewThreadBox.setLayoutData(postNewThreadBoxData);
		
		postNewThreadBox.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setReply(postNewThreadBox.getText());
				
			}
		});
		
		Button postButton = new Button(replyComposite, SWT.BORDER);
		postButton.setText("submit");
		postButton.addSelectionListener(new ReplyListener());
		

	//	Controller.getInstance().subscribeToLighthouseEvents(this);

	}
	
	public void setReply(String reply) {
		this.reply = reply;
	}

	public String getReply() {
		return reply;
	}
	
	private class ReplyListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			Post newPost = new Post(true, "", reply, tm);
			Post replyeePost = thread.getRootQuestion();

			if (replyeePost != null) {
				replyeePost.addResponse(newPost);
				postNewThreadBox.setText("");
				updateView();
			}
			pu.run();
		}
	}
	
	public void updateView(){
		listOfReplies.clearChildren();
		for(Post post : thread.getRootQuestion().getResponses()){	
			ForumElement composite = new ForumElement(this, SWT.None);
			GridData data = new GridData(LayoutMetrics.POST_VIEW_WIDTH,
				LayoutMetrics.POST_VIEW_HEIGHT);
			composite.setLayoutData(data);
			composite.setLayout(new GridLayout(1,false));
			Label replyLabel = new Label(composite, SWT.None);
			replyLabel.setText(post.getMessage());		
			
			listOfReplies.add(composite);
			listOfReplies.renderList();
		}
		listOfReplies.setSize(listOfReplies.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.layout();
	}

	@Override
	public void receive(List<LighthouseEvent> events) {
		updateView();
		
	}
	

}
