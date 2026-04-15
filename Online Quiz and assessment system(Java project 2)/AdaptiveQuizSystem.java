import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

class QuizQuestion {
    private String questionText;
    private String[] options;
    private char correctAnswer;
    private String difficulty;
    public QuizQuestion(String questionText, String[] options, char correctAnswer, String difficulty) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = Character.toUpperCase(correctAnswer);
        this.difficulty = difficulty;
    }
    public String getQuestionText() {
        return questionText;
    }
    public String[] getOptions() {
        return options;
    }
    public boolean isAnswerCorrect(char selectedChoice) {
        return Character.toUpperCase(selectedChoice) == correctAnswer;
    }
    public String getDifficulty() {
        return difficulty;
    }
}

public class AdaptiveQuizSystem extends JFrame {
    private static ArrayList<QuizQuestion> loadQuestionsFromFile(String filePath) {
        ArrayList<QuizQuestion> questions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 7) {
                    String questionText = parts[0];
                    String[] options = {parts[1], parts[2], parts[3], parts[4]};
                    char correctAnswer = parts[5].charAt(0);
                    String difficulty = parts[6];
                    questions.add(new QuizQuestion(questionText, options, correctAnswer, difficulty));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading questions from file: " + e.getMessage());
        }
        return questions;
    }
    private ArrayList<QuizQuestion> selectedQuestions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int timeLeft = 15;
    private Timer questionTimer;
    private String studentName;
    private String difficultyLevel;
    private JLabel titleLabel;
    private JLabel timerLabel;
    private JTextArea questionArea;
    private JRadioButton optionA;
    private JRadioButton optionB;
    private JRadioButton optionC;
    private JRadioButton optionD;
    private ButtonGroup choicesGroup;
    private JButton nextButton;
    private JButton quitButton;
    public AdaptiveQuizSystem(String studentName, ArrayList<QuizQuestion> selectedQuestions) {
        this.studentName = studentName;
        this.selectedQuestions = selectedQuestions;
        this.difficultyLevel = selectedQuestions.isEmpty() ? "Unknown" : selectedQuestions.get(0).getDifficulty();
        setTitle("Adaptive Quiz System");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        buildInterface();
        loadQuestion();
        startTimer();
        setVisible(true);
    }
    private void buildInterface() {
        titleLabel = new JLabel("Online Quiz Assessment", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        timerLabel = new JLabel("Time Left: 15 sec");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        centerPanel.add(timerLabel);
        questionArea= new JTextArea();
        questionArea.setEditable(false);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setFont(new Font("Arial", Font.PLAIN, 18));
        questionArea.setBackground(getBackground());
        centerPanel.add(questionArea);
        optionA = new JRadioButton();
        optionB = new JRadioButton();
        optionC = new JRadioButton();
        optionD = new JRadioButton();
        choicesGroup = new ButtonGroup();
        choicesGroup.add(optionA);
        choicesGroup.add(optionB);
        choicesGroup.add(optionC);
        choicesGroup.add(optionD);
        centerPanel.add(optionA);
        centerPanel.add(optionB);
        centerPanel.add(optionC);
        centerPanel.add(optionD);
        add(centerPanel, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel();
        nextButton= new JButton("Next");
        quitButton = new JButton("Quit Quiz");
        bottomPanel.add(nextButton);
        bottomPanel.add(quitButton);
        add(bottomPanel, BorderLayout.SOUTH);
        nextButton.addActionListener(e -> processAnswer());
        quitButton.addActionListener(e -> {
            saveResultAutomatically();
            JOptionPane.showMessageDialog(this, "Quiz exited. Result saved automatically.");
            System.exit(0);
        });
    }
    private void loadQuestion() {
        if (currentQuestionIndex >= selectedQuestions.size()) {
            finishQuiz();
            return;
        }
        QuizQuestion currentQuestion = selectedQuestions.get(currentQuestionIndex);
        questionArea.setText(
                "[" + currentQuestion.getDifficulty() + "] " +
                currentQuestion.getQuestionText()
        );
        String[] options = currentQuestion.getOptions();
        optionA.setText(options[0]);
        optionB.setText(options[1]);
        optionC.setText(options[2]);
        optionD.setText(options[3]);
        choicesGroup.clearSelection();
        timeLeft = 15;
        timerLabel.setText("Time Left: " + timeLeft + " sec");
    }

    private void processAnswer() {
        QuizQuestion currentQuestion = selectedQuestions.get(currentQuestionIndex);
        char selectedChoice = 'X';
        if (optionA.isSelected()) selectedChoice = 'A';
        if (optionB.isSelected()) selectedChoice = 'B';
        if (optionC.isSelected()) selectedChoice = 'C';
        if (optionD.isSelected()) selectedChoice = 'D';
        if (currentQuestion.isAnswerCorrect(selectedChoice)) {
            score++;
        }
        currentQuestionIndex++;
        loadQuestion();
    }
    private void startTimer() {
        questionTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                timerLabel.setText("Time Left: " + timeLeft + " sec");

                if (timeLeft <= 0) {
                    processAnswer();
                }
            }
        });
        questionTimer.start();
    }
    private void finishQuiz() {
        questionTimer.stop();
        saveResultAutomatically();
        double percentage = (score * 100.0) / selectedQuestions.size();
        String performance;
        if (percentage >= 80) {
            performance = "Excellent";
        } else if (percentage >= 50) {
            performance = "Good";
        } else {
            performance = "Needs Improvement";
        }
        JOptionPane.showMessageDialog(
                this,
                "Quiz Completed!\n" +
                "Student: " + studentName + "\n" +
                "Score: " + score + "/" + selectedQuestions.size() + "\n" +
                "Percentage: " + percentage + "%\n" +
                "Performance: " + performance
        );
        System.exit(0);
    }
    private void saveResultAutomatically() {
        try {
            File resultsFile = new File("Online Quiz and assessment system", "quiz_results.txt");
            FileWriter writer = new FileWriter(resultsFile, true);
            writer.write(
                    "Student: " + studentName +
                    " | Score: " + score + "/" + selectedQuestions.size() +
                    " | Difficulty: " + difficultyLevel +
                    "\n"
            );
            writer.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not save results.");
        }
    }

    public static void main(String[] args) {
        String studentName = JOptionPane.showInputDialog("Enter your name:");
        String[] difficultyOptions = {"Easy", "Medium", "Hard"};
        String selectedLevel = (String) JOptionPane.showInputDialog(
                null,
                "Choose Difficulty Level:",
                "Quiz Setup",
                JOptionPane.QUESTION_MESSAGE,
                null,
                difficultyOptions,
                difficultyOptions[0]
        );
        ArrayList<QuizQuestion> allQuestions = new ArrayList<>();
        ArrayList<QuizQuestion> filteredQuestions = new ArrayList<>();
        File questionsFile = new File("Online Quiz and assessment system", "questions.txt");
        allQuestions = loadQuestionsFromFile(questionsFile.getPath());
        for (QuizQuestion question : allQuestions) {
            if (question.getDifficulty().equals(selectedLevel)) {
                filteredQuestions.add(question);
            }
        }
        if (filteredQuestions.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No questions available for selected level.");
            return;
        }
        new AdaptiveQuizSystem(studentName, filteredQuestions);
    }
}
