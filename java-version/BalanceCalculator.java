import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 剩余金额计算器
 * 这是一个用于计算剩余金额的桌面应用程序
 * 功能包括：
 * 1. 日期选择（自定义日历）
 * 2. 时间单位选择（年、季度、月、184天）
 * 3. 总价格输入
 * 4. 剩余金额计算
 * 5. 重置功能
 */
public class BalanceCalculator extends JFrame {
    // 常量定义
    private static final String[] WEEK_DAYS = {"日", "一", "二", "三", "四", "五", "六"};
    private static final String[] MONTH_NAMES = {"一月", "二月", "三月", "四月", "五月", "六月", 
                                               "七月", "八月", "九月", "十月", "十一月", "十二月"};

    // 界面组件
    private JTextField priceField;      // 价格输入框
    private JTextField dateField;       // 日期输入框
    private ButtonGroup timeUnitGroup;  // 时间单位按钮组
    private JRadioButton yearButton, quarterButton, monthButton, days184Button;  // 时间单位选择按钮
    private JTextArea resultArea;       // 结果显示区域
    private JScrollPane scrollPane;     // 结果区域滚动面板
    
    // 数据字段
    private double selectedDays = 0;    // 选中的总天数
    
    // 日历对话框组件
    private JPanel calendarPanel;       // 日历面板
    private JDialog calendarDialog;     // 日历对话框
    private JLabel monthLabel;          // 月份显示标签
    private int currentMonth;           // 当前显示的月份
    private int currentYear;            // 当前显示的年份
    private JDialog overlayDialog;      // 添加为类成员变量
    
    /**
     * 构造函数
     * 初始化界面和所有组件
     */
    public BalanceCalculator() {
        // 设置窗口基本属性
        setTitle("剩余金额计算");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));  // 使用边框布局，组件间距10像素
        setSize(700, 600);  // 设置窗口大小

        // 创建主面板，使用BoxLayout实现垂直布局
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));  // 设置边距

        // 配置键盘快捷键
        // 注册Enter键用于计算，Esc键用于重置
        KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        KeyStroke escKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        
        // 将快捷键绑定到整个窗口范围（WHEN_IN_FOCUSED_WINDOW）
        mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enterKey, "calculate");
        mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escKey, "reset");
        
        // 设置快捷键对应的动作
        mainPanel.getActionMap().put("calculate", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculate();  // Enter键触发计算
            }
        });
        
        mainPanel.getActionMap().put("reset", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetForm();  // Esc键触发重置
            }
        });

        // 创建标题面板
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("剩余金额计算");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 26));  // 设置标题字体
        titlePanel.add(titleLabel);

        // 创建日期选择面板
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.add(new JLabel("选择开通日期："));
        dateField = new JTextField(15);
        dateField.setEditable(false);  // 设置为不可编辑，只能通过日历选择
        dateField.setBackground(Color.WHITE);
        dateField.setBorder(BorderFactory.createCompoundBorder(
            dateField.getBorder(), 
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        datePanel.add(dateField);
        
        // 创建日历按钮
        JButton calendarButton = new JButton("选择日期");
        calendarButton.setFocusPainted(false);
        calendarButton.setPreferredSize(new Dimension(100, 35));
        calendarButton.addActionListener(e -> showCalendarDialog());
        
        datePanel.add(calendarButton);

        // 初始化日历对话框
        initCalendarDialog();

        // 时间单位选择面板
        JPanel timeUnitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timeUnitPanel.add(new JLabel("选择时间单位："));
        timeUnitGroup = new ButtonGroup();
        yearButton = createStyledRadioButton("年", 365);
        quarterButton = createStyledRadioButton("季度", 90);
        monthButton = createStyledRadioButton("月", 30);
        days184Button = createStyledRadioButton("184天", 184);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.add(yearButton);
        radioPanel.add(quarterButton);
        radioPanel.add(monthButton);
        radioPanel.add(days184Button);
        timeUnitPanel.add(radioPanel);

        // 价格输入面板
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pricePanel.add(new JLabel("输入价格："));
        priceField = new JTextField(15);
        priceField.setBorder(BorderFactory.createCompoundBorder(
            priceField.getBorder(), 
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        pricePanel.add(priceField);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        
        // 计算按钮
        JButton calculateButton = new JButton("计算 (Enter)");
        calculateButton.setPreferredSize(new Dimension(140, 45));
        calculateButton.setBackground(new Color(64, 158, 255));    // 使用更亮的蓝色作为默认颜色
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setFocusPainted(false);
        calculateButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        calculateButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(24, 144, 255), 1),  // 添加深蓝色边框
            BorderFactory.createEmptyBorder(4, 14, 4, 14)
        ));
        
        // 添加鼠标悬停效果
        calculateButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                calculateButton.setBackground(new Color(24, 144, 255));  // 悬停时颜色变深
                calculateButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(24, 144, 255), 1),
                    BorderFactory.createEmptyBorder(4, 14, 4, 14)
                ));
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                calculateButton.setBackground(new Color(64, 158, 255));  // 恢复较亮的蓝色
                calculateButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(24, 144, 255), 1),
                    BorderFactory.createEmptyBorder(4, 14, 4, 14)
                ));
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            @Override
            public void mousePressed(MouseEvent e) {
                calculateButton.setBackground(new Color(9, 109, 217));   // 点击时颜色更深
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                calculateButton.setBackground(new Color(24, 144, 255));  // 释放时恢复悬停颜色
            }
        });
        calculateButton.addActionListener(e -> calculate());

        // 重置按钮
        JButton resetButton = new JButton("重置 (Esc)");
        resetButton.setPreferredSize(new Dimension(140, 45));  
        resetButton.setBackground(new Color(245, 245, 245));
        resetButton.setForeground(new Color(71, 71, 71));
        resetButton.setFocusPainted(false);
        resetButton.setFont(new Font("微软雅黑", Font.BOLD, 16));  
        resetButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(217, 217, 217), 1),
            BorderFactory.createEmptyBorder(4, 14, 4, 14)
        ));

        // 添加鼠标悬停效果
        resetButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                resetButton.setBackground(new Color(250, 250, 250));
                resetButton.setForeground(new Color(24, 144, 255));  
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                resetButton.setBackground(new Color(245, 245, 245));
                resetButton.setForeground(new Color(71, 71, 71));
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        resetButton.addActionListener(e -> resetForm());

        buttonPanel.add(calculateButton);
        buttonPanel.add(Box.createHorizontalStrut(15));  
        buttonPanel.add(resetButton);

        // 结果显示区域
        resultArea = new JTextArea(14, 40);  
        resultArea.setEditable(false);
        resultArea.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        resultArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        
        scrollPane = new JScrollPane(resultArea);  // 将scrollPane赋值给类成员变量
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.setPreferredSize(new Dimension(650, 300));  

        // 添加所有组件到主面板
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(datePanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(timeUnitPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(pricePanel);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(scrollPane);

        // 设置主面板背景色
        mainPanel.setBackground(new Color(240, 242, 245));
        
        add(mainPanel);
        setLocationRelativeTo(null);
    }

    /**
     * 创建样式化的单选按钮
     * @param text 按钮文本
     * @param days 按钮对应的天数
     * @return 样式化的单选按钮
     */
    private JRadioButton createStyledRadioButton(String text, int days) {
        JRadioButton button = new JRadioButton(text);
        button.setFocusPainted(false);
        button.addActionListener(e -> selectedDays = days);
        timeUnitGroup.add(button);
        return button;
    }

    /**
     * 初始化日历对话框
     */
    private void initCalendarDialog() {
        calendarDialog = new JDialog(this);
        calendarDialog.setUndecorated(true);
        calendarDialog.setLayout(new BorderLayout());
        calendarDialog.setSize(300, 300);
        calendarDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // 创建一个带边框的面板作为容器
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        container.setBackground(Color.WHITE);
        calendarDialog.setContentPane(container);

        // 初始化当前日期
        Calendar cal = Calendar.getInstance();
        currentMonth = cal.get(Calendar.MONTH);
        currentYear = cal.get(Calendar.YEAR);

        // 创建月份选择面板
        JPanel monthChooser = new JPanel(new FlowLayout());
        JButton prevMonth = new JButton("◀");
        JButton nextMonth = new JButton("▶");
        monthLabel = new JLabel("", SwingConstants.CENTER);
        
        // 设置按钮样式
        prevMonth.setFocusPainted(false);
        nextMonth.setFocusPainted(false);
        
        monthChooser.add(prevMonth);
        monthChooser.add(monthLabel);
        monthChooser.add(nextMonth);

        // 创建星期标签面板
        JPanel weekDaysPanel = new JPanel(new GridLayout(1, 7));
        for (String weekDay : WEEK_DAYS) {
            JLabel label = new JLabel(weekDay, SwingConstants.CENTER);
            weekDaysPanel.add(label);
        }

        // 创建日历面板
        calendarPanel = new JPanel(new GridLayout(6, 7));
        
        // 添加事件监听器
        prevMonth.addActionListener(e -> {
            currentMonth--;
            if (currentMonth < 0) {
                currentMonth = 11;
                currentYear--;
            }
            updateCalendar();
        });

        nextMonth.addActionListener(e -> {
            currentMonth++;
            if (currentMonth > 11) {
                currentMonth = 0;
                currentYear++;
            }
            updateCalendar();
        });

        // 组装日历对话框
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(monthChooser, BorderLayout.CENTER);
        topPanel.add(weekDaysPanel, BorderLayout.SOUTH);

        container.add(topPanel, BorderLayout.NORTH);
        container.add(calendarPanel, BorderLayout.CENTER);

        updateCalendar();
    }

    /**
     * 更新日历面板
     */
    private void updateCalendar() {
        calendarPanel.removeAll();
        monthLabel.setText(MONTH_NAMES[currentMonth] + " " + currentYear);

        Calendar cal = Calendar.getInstance();
        cal.set(currentYear, currentMonth, 1);
        
        int firstDayOfMonth = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // 获取今天的日期
        Calendar today = Calendar.getInstance();
        
        // 添加空白天数
        for (int i = 0; i < firstDayOfMonth; i++) {
            calendarPanel.add(new JLabel(""));
        }

        // 添加日期按钮
        for (int day = 1; day <= daysInMonth; day++) {
            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.setFocusPainted(false);
            
            // 设置按钮样式
            cal.set(Calendar.DAY_OF_MONTH, day);
            if (cal.after(today)) {
                dayButton.setEnabled(false);
            } else {
                final int selectedDay = day;
                dayButton.addActionListener(e -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(currentYear, currentMonth, selectedDay);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    dateField.setText(sdf.format(selectedDate.getTime()));
                    closeCalendarDialog();
                });
            }
            
            calendarPanel.add(dayButton);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    /**
     * 显示日历对话框
     */
    private void showCalendarDialog() {
        // 创建一个半透明的覆盖层
        overlayDialog = new JDialog(this);
        overlayDialog.setUndecorated(true);
        overlayDialog.setBackground(new Color(0, 0, 0, 0));
        
        // 创建透明面板
        JPanel overlayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 1));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlayPanel.setOpaque(false);
        overlayDialog.setContentPane(overlayPanel);
        
        // 设置覆盖层大小和位置
        Rectangle screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getMaximumWindowBounds();
        overlayDialog.setBounds(screenBounds);
        
        // 添加点击事件处理
        overlayPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                closeCalendarDialog();
            }
        });
        
        // 设置日历对话框的位置
        Point loc = dateField.getLocationOnScreen();
        calendarDialog.setLocation(loc.x, loc.y + dateField.getHeight());
        
        // 确保日历对话框始终在覆盖层之上
        calendarDialog.setModalityType(Dialog.ModalityType.MODELESS);
        overlayDialog.setVisible(true);
        calendarDialog.setVisible(true);
        calendarDialog.toFront();
        
        // 添加窗口监听器，确保同步关闭
        calendarDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeCalendarDialog();
            }
        });
    }
    
    /**
     * 关闭日历对话框和覆盖层
     */
    private void closeCalendarDialog() {
        if (calendarDialog != null) {
            calendarDialog.setVisible(false);
            calendarDialog.dispose();
        }
        if (overlayDialog != null) {
            overlayDialog.setVisible(false);
            overlayDialog.dispose();
        }
    }

    /**
     * 计算剩余金额
     * 步骤：
     * 1. 验证输入
     * 2. 计算已过去的天数
     * 3. 计算剩余天数
     * 4. 计算每天的价格
     * 5. 计算剩余金额
     * 6. 显示结果
     */
    private void calculate() {
        try {
            // 验证时间单位是否选择
            if (selectedDays == 0) {
                JOptionPane.showMessageDialog(this, "请选择时间单位！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 验证日期是否选择
            if (dateField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "请选择开通日期！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 验证价格输入
            double price = Double.parseDouble(priceField.getText());
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "请输入有效的价格！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 解析日期并验证
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date selectedDate = sdf.parse(dateField.getText());
            Date today = new Date();

            // 确保选择的是过去的日期
            if (selectedDate.after(today)) {
                JOptionPane.showMessageDialog(this, "请选择一个过去的日期！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 计算每天的价格
            double pricePerDay = price / selectedDays;
            
            // 计算已过去的天数
            long diffInMillies = today.getTime() - selectedDate.getTime();
            long passedDays = diffInMillies / (1000 * 60 * 60 * 24);
            
            // 计算剩余天数（不能小于0）
            double remainingDays = Math.max(0, selectedDays - passedDays);
            
            // 计算剩余金额
            double remainingAmount = pricePerDay * remainingDays;

            // 格式化显示结果
            StringBuilder result = new StringBuilder();
            result.append("计算结果：\n");
            result.append(String.format("开通日期：%s    ", dateField.getText()));
            result.append(String.format("已过去：%d天    ", passedDays));
            result.append(String.format("剩余：%.0f天    ", remainingDays));
            result.append(String.format("每天价格：¥%.2f", pricePerDay));
            
            // 添加分隔线
            result.append("\n----------------------------------------\n\n");
            
            // 添加剩余金额显示
            String amountStr = String.format("%.2f", remainingAmount);
            result.append("剩余金额：\n");
            result.append(String.format("¥%s", amountStr));

            // 更新结果显示区域
            resultArea.setText(result.toString());
            resultArea.setFont(new Font("微软雅黑", Font.PLAIN, 15));
            resultArea.setCaretPosition(0);  // 滚动到顶部
            
            // 使用定时器延迟设置金额的特殊样式
            Timer timer = new Timer(50, e -> {
                try {
                    // 获取金额的起始位置
                    int lastLineStart = result.lastIndexOf("¥");
                    if (lastLineStart >= 0) {
                        // 创建金额的样式属性
                        SimpleAttributeSet attrs = new SimpleAttributeSet();
                        StyleConstants.setFontSize(attrs, 24);     // 设置字体大小
                        StyleConstants.setFontFamily(attrs, "微软雅黑");
                        StyleConstants.setBold(attrs, true);       // 设置为粗体
                        StyleConstants.setForeground(attrs, new Color(24, 144, 255));  // 设置颜色
                        
                        // 创建新的带样式的文本面板
                        JTextPane textPane = new JTextPane();
                        textPane.setEditable(false);
                        textPane.setText(result.toString());
                        textPane.setFont(new Font("微软雅黑", Font.PLAIN, 15));
                        textPane.setBackground(resultArea.getBackground());
                        textPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
                        
                        // 应用样式到金额文本
                        StyledDocument doc = textPane.getStyledDocument();
                        doc.setCharacterAttributes(lastLineStart, amountStr.length() + 1, attrs, false);
                        
                        // 更新显示
                        scrollPane.setViewportView(textPane);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            timer.setRepeats(false);  // 设置定时器只执行一次
            timer.start();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "请输入有效的价格！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 重置表单
     * 清空所有输入和结果显示
     */
    private void resetForm() {
        dateField.setText("");          // 清空日期
        priceField.setText("");        // 清空价格
        timeUnitGroup.clearSelection(); // 清空时间单位选择
        selectedDays = 0;              // 重置选中天数
        resultArea.setText("");        // 清空结果显示
    }

    /**
     * 主方法
     * 创建并显示GUI
     */
    public static void main(String[] args) {
        // 设置Swing的外观为系统默认外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 在事件调度线程中创建并显示GUI
        SwingUtilities.invokeLater(() -> {
            BalanceCalculator calculator = new BalanceCalculator();
            calculator.setVisible(true);
        });
    }
}
