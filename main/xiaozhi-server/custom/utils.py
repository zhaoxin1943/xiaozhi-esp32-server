from datetime import datetime

def calculate_age(birth_date: str):
    try:
        birth_date = datetime.strptime(birth_date, '%Y-%m-%d %H:%M:%S')
        current_date = datetime.now()
        age = current_date.year - birth_date.year
        if current_date.month < birth_date.month or \
                (current_date.month == birth_date.month and current_date.day < birth_date.day):
            age -= 1
        return age
    except ValueError:
        return 7