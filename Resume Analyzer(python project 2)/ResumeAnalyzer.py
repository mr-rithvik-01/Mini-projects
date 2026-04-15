import nltk
import string
from collections import Counter
from PyPDF2 import PdfReader
nltk.download('punkt')

def read_resume_content(file_path):
    if file_path.endswith(".txt"):
        with open(file_path, "r", encoding="utf-8") as file:
            return file.read()
    elif file_path.endswith(".pdf"):
        pdf_reader = PdfReader(file_path)
        extracted_text = ""

        for page in pdf_reader.pages:
            extracted_text += page.extract_text()

        return extracted_text
    else:
        print("Unsupported file type.")
        return ""

def prepare_text_for_analysis(raw_text):
    lower_text = raw_text.lower()
    for symbol in string.punctuation:
        lower_text = lower_text.replace(symbol, "")
    return lower_text

def collect_resume_keywords(clean_text):
    words = nltk.word_tokenize(clean_text)
    ignored_words = {
        "the", "is", "and", "in", "to", "of", "a",
        "for", "with", "on", "an", "at", "by"
    }
    useful_words = [
        word for word in words
        if word not in ignored_words and word.isalpha()
    ]
    return useful_words

def compare_resume_with_job(resume_words, required_skills):
    found_skills = []
    missing_skills = []
    resume_word_set = set(resume_words)
    for skill in required_skills:
        if skill.lower() in resume_word_set:
            found_skills.append(skill)
        else:
            missing_skills.append(skill)
    return found_skills, missing_skills

def calculate_match_percentage(found_skills, total_skills):
    if total_skills == 0:
        return 0
    return round((len(found_skills) / total_skills) * 100, 2)

def show_analysis_report(found_skills, missing_skills, match_score):
    print("\n========== Resume Analysis Report ==========")
    print(f"\nMatch Score: {match_score}%")
    print("\nMatched Skills:")
    if found_skills:
        for skill in found_skills:
            print(f"- {skill}")
    else:
        print("No matching skills found.")
    print("\nSuggested Skills To Add:")
    if missing_skills:
        for skill in missing_skills:
            print(f"- {skill}")
    else:
        print("Your resume matches all required skills!")
    print("\n============================================")

def main():
    print("===== Resume Analyzer & Job Matcher =====")
    resume_file = input(
        "Enter resume file path (.txt or .pdf): "
    )
    target_job_skills = [
        "Python",
        "SQL",
        "Machine Learning",
        "Communication",
        "Data Analysis",
        "Java"
    ]
    resume_text = read_resume_content(resume_file)
    if not resume_text:
        return
    cleaned_resume = prepare_text_for_analysis(resume_text)
    extracted_words = collect_resume_keywords(cleaned_resume)
    matched_skills, missing_skills = compare_resume_with_job(
        extracted_words,
        target_job_skills
    )
    compatibility_score = calculate_match_percentage(
        matched_skills,
        len(target_job_skills)
    )
    show_analysis_report(
        matched_skills,
        missing_skills,
        compatibility_score
    )

if __name__ == "__main__":
    main()