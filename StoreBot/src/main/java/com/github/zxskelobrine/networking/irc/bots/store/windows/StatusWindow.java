package com.github.zxskelobrine.networking.irc.bots.store.windows;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import com.github.zxskelobrine.networking.irc.bots.store.StoreBot;
import com.github.zxskelobrine.networking.irc.bots.store.systems.SystemsManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class StatusWindow extends JFrame {

	private static final long serialVersionUID = 8796384378225007621L;
	private boolean running = true;
	private Thread thread;
	private JCheckBox chckbxKarmaSystem;
	private JCheckBox chckbxMailSystem;
	private JCheckBox chckbxNorwaySystem;
	private JCheckBox chckbxLyricSystem;
	private JCheckBox chckbxRiotSystem;
	private JButton btnTerminate;
	private JCheckBox chckbxIdiotSystem;
	private JSeparator separator;
	private JLabel lblEnabledServices;
	private JPanel contentPane;
	private static StoreBot storeBot;

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StatusWindow frame = new StatusWindow();
					frame.setVisible(true);
					Thread thread2 = new Thread() {
						@Override
						public void run() {
							try {
								storeBot = new StoreBot();
								StoreBot.main(args, storeBot);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					};
					thread2.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public StatusWindow() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		setTitle("VitBot - Status");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 242, 235);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		lblEnabledServices = new JLabel("Enabled Services");
		lblEnabledServices.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblEnabledServices = new GridBagConstraints();
		gbc_lblEnabledServices.insets = new Insets(0, 0, 5, 5);
		gbc_lblEnabledServices.gridx = 0;
		gbc_lblEnabledServices.gridy = 0;
		contentPane.add(lblEnabledServices, gbc_lblEnabledServices);

		separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.insets = new Insets(0, 0, 5, 5);
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 1;
		contentPane.add(separator, gbc_separator);

		chckbxIdiotSystem = new JCheckBox("Idiot System");
		chckbxIdiotSystem.setEnabled(false);
		GridBagConstraints gbc_chckbxIdiotSystem = new GridBagConstraints();
		gbc_chckbxIdiotSystem.anchor = GridBagConstraints.WEST;
		gbc_chckbxIdiotSystem.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxIdiotSystem.gridx = 0;
		gbc_chckbxIdiotSystem.gridy = 2;
		contentPane.add(chckbxIdiotSystem, gbc_chckbxIdiotSystem);

		btnTerminate = new JButton("Terminate");
		btnTerminate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				end();
			}
		});
		GridBagConstraints gbc_btnTerminate = new GridBagConstraints();
		gbc_btnTerminate.insets = new Insets(0, 0, 5, 0);
		gbc_btnTerminate.gridx = 2;
		gbc_btnTerminate.gridy = 2;
		contentPane.add(btnTerminate, gbc_btnTerminate);

		chckbxRiotSystem = new JCheckBox("Riot System");
		chckbxRiotSystem.setEnabled(false);
		GridBagConstraints gbc_chckbxRiotSystem = new GridBagConstraints();
		gbc_chckbxRiotSystem.anchor = GridBagConstraints.WEST;
		gbc_chckbxRiotSystem.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxRiotSystem.gridx = 0;
		gbc_chckbxRiotSystem.gridy = 3;
		contentPane.add(chckbxRiotSystem, gbc_chckbxRiotSystem);

		chckbxLyricSystem = new JCheckBox("Lyric System");
		chckbxLyricSystem.setEnabled(false);
		GridBagConstraints gbc_chckbxLyricSystem = new GridBagConstraints();
		gbc_chckbxLyricSystem.anchor = GridBagConstraints.WEST;
		gbc_chckbxLyricSystem.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxLyricSystem.gridx = 0;
		gbc_chckbxLyricSystem.gridy = 4;
		contentPane.add(chckbxLyricSystem, gbc_chckbxLyricSystem);

		chckbxNorwaySystem = new JCheckBox("Norway System");
		chckbxNorwaySystem.setEnabled(false);
		GridBagConstraints gbc_chckbxNorwaySystem = new GridBagConstraints();
		gbc_chckbxNorwaySystem.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxNorwaySystem.gridx = 0;
		gbc_chckbxNorwaySystem.gridy = 5;
		contentPane.add(chckbxNorwaySystem, gbc_chckbxNorwaySystem);

		chckbxMailSystem = new JCheckBox("Mail System");
		chckbxMailSystem.setEnabled(false);
		GridBagConstraints gbc_chckbxMailSystem = new GridBagConstraints();
		gbc_chckbxMailSystem.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxMailSystem.anchor = GridBagConstraints.WEST;
		gbc_chckbxMailSystem.gridx = 0;
		gbc_chckbxMailSystem.gridy = 6;
		contentPane.add(chckbxMailSystem, gbc_chckbxMailSystem);

		chckbxKarmaSystem = new JCheckBox("Karma System");
		chckbxKarmaSystem.setEnabled(false);
		GridBagConstraints gbc_chckbxKarmaSystem = new GridBagConstraints();
		gbc_chckbxKarmaSystem.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxKarmaSystem.anchor = GridBagConstraints.WEST;
		gbc_chckbxKarmaSystem.gridx = 0;
		gbc_chckbxKarmaSystem.gridy = 7;
		contentPane.add(chckbxKarmaSystem, gbc_chckbxKarmaSystem);
		startUpdateThread();
	}

	public void startUpdateThread() {
		thread = new Thread() {
			@Override
			public void run() {
				while (running) {
					chckbxIdiotSystem.setSelected(SystemsManager.isIdiotSystemEnabled());
					chckbxKarmaSystem.setSelected(SystemsManager.isKarmaSystemEnabled());
					chckbxLyricSystem.setSelected(SystemsManager.isLyricSystemEnabled());
					chckbxMailSystem.setSelected(SystemsManager.isMailSystemEnabled());
					chckbxNorwaySystem.setSelected(SystemsManager.isNorwaySystemEnabled());
					chckbxRiotSystem.setSelected(SystemsManager.isRiotSystemEnabled());
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();
	}

	public void end() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		storeBot.disableManager();
	}
}
