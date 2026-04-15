import os
from datetime import datetime

def read_log_file(file_path):
    try:
        with open(file_path, "r") as file:
            return file.readlines()
    except FileNotFoundError:
        print("Log file not found. Please check the file path.")
        return []

def filter_security_events(log_lines):
    important_keywords = ["CRITICAL", "ERROR", "FAILED LOGIN"]
    filtered_lines = []
    event_counter = {
        "CRITICAL": 0,
        "ERROR": 0,
        "FAILED LOGIN": 0
    }
    for line in log_lines:
        line_upper = line.upper()
        for keyword in important_keywords:
            if keyword in line_upper:
                filtered_lines.append(line)
                event_counter[keyword] += 1
                break
    return filtered_lines, event_counter

def generate_report(filtered_lines):
    today_date = datetime.now().strftime("%Y-%m-%d")
    report_name = f"security_alert_{today_date}.txt"
    script_dir = os.path.dirname(os.path.abspath(__file__))
    report_path = os.path.join(script_dir, report_name)
    with open(report_path, "w") as report_file:
        report_file.writelines(filtered_lines)
    return report_path

def display_summary(counter_dict):
    print("\n--- Security Summary ---")
    for event, count in counter_dict.items():
        print(f"{event}: {count}")

def check_report_file(report_name):
    if os.path.exists(report_name):
        file_size = os.path.getsize(report_name)
        print(f"\nReport generated successfully: {report_name}")
        print(f"File size: {file_size} bytes")
    else:
        print("Report generation failed.")

def main():
    log_file_path = "OpsBot/server.log"
    print("Reading log file...")
    log_data = read_log_file(log_file_path)
    if not log_data:
        return
    print("Filtering important events...")
    important_logs, event_counts = filter_security_events(log_data)
    print("Generating report...")
    report_file = generate_report(important_logs)
    display_summary(event_counts)
    check_report_file(report_file)

if __name__ == "__main__":
    main()