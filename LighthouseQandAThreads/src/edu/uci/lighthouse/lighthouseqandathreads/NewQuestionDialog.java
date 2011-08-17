package edu.uci.lighthouse.lighthouseqandathreads;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import edu.uci.lighthouse.lighthouseqandathreads.model.Forum;
import edu.uci.lighthouse.lighthouseqandathreads.model.Post;
import edu.uci.lighthouse.lighthouseqandathreads.model.PostTreeItem;

import edu.uci.lighthouse.lighthouseqandathreads.model.Thread;

public class NewQuestionDialog extends MessageDialog {

	private String question;

	public static int OK = 0;
	public static int CANCEL = 1;
	private static String[] labelArray = { "OK", "CANCEL" };
	private Tree tree;
	
	//TODO: only a test attribute will be removed
	private Forum testForum;

	public NewQuestionDialog(Shell parentShell, String dialogTitle,
			Image dialogTitleImage, String dialogMessage, int dialogImageType,
			int defaultIndex) {

		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage,
				dialogImageType, labelArray, defaultIndex);
	}

	public Control createCustomArea(Composite parent) {

		GridData data = new GridData(GridData.FILL_HORIZONTAL);

		TabFolder tabFolder = new TabFolder(parent, SWT.BORDER);

		TabItem tabItem = new TabItem(tabFolder, SWT.NULL);
		tabItem.setText("Ask a question");
		createQuestionComposite(tabFolder, tabItem);

		TabItem tabItem2 = new TabItem(tabFolder, SWT.NULL);
		tabItem2.setText("Threads");
		createThreadComposite(tabFolder, tabItem2);

		return tabFolder;
	}

	private void createThreadComposite(TabFolder tabFolder, TabItem tabItem) {
		GridData compsiteData = new GridData(650, 450);

		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(compsiteData);

		tabItem.setControl(composite);

		GridData questionLayoutData = new GridData(581, 380);

		tree = new Tree(composite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL
				| SWT.H_SCROLL);
		tree.setLayoutData(questionLayoutData);

		tree.addSelectionListener(new ListListener());
		
		GridData replyBoxData = new GridData(600, 100);
		final StyledText replyBox = new StyledText(composite, SWT.BORDER);
		replyBox.setLayoutData(replyBoxData);

		replyBox.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				

			}
		});
		
		populateTree(testForum);

	}

	public void setTestForm(Forum test){
		this.testForum = test;
	}
	
	private class ListListener extends SelectionAdapter {

		public void widgetSelected(SelectionEvent e) {

		}
	}

	private void createQuestionComposite(TabFolder tabFolder, TabItem tabItem) {

		GridData compsiteData = new GridData(650, 450);

		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(compsiteData);

		tabItem.setControl(composite);

		GridData questionLayoutData = new GridData(600, 400);
		final StyledText st = new StyledText(composite, SWT.BORDER);
		st.setLayoutData(questionLayoutData);

		st.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setQuestion(st.getText());

			}
		});

	}
	

	private void populateTree(Forum forum){
		for(Thread thread : forum.getThreads()){
			setupTreeBranch(thread);
		}
	}
	
	private void setupTreeBranch(Thread thread){
		Post rootPost = thread.getRootQuestion();
		
		PostTreeItem item = new PostTreeItem(rootPost,tree,0);
		item.setText(thread.getRootQuestion().getSubject());
		
		List<Post> posts = thread.getRootQuestion().getResponses();
		for(Post child: posts){
			PostTreeItem childItem = new PostTreeItem(child,item,0);
			childItem.setText(child.getSubject());
			setupSubTreeBranch(child, childItem);
		}
	}

	private void setupSubTreeBranch(Post post, TreeItem parentItem){
		List<Post> children = post.getResponses();
		for(Post child: children){
			PostTreeItem childItem = new PostTreeItem(child,parentItem,0);
			childItem.setText(child.getSubject());
			setupSubTreeBranch(child, childItem);
		}
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getQuestion() {
		return question;
	}

}
