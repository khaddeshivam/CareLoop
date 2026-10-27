-- Insert patients
INSERT INTO patients (blood_group, date_of_birth, patient_insurance_id, first_name, gender, last_name, phone)
VALUES ('O_POSITIVE', '1990-05-12', NULL, 'Alice', 'Female', 'Green', '555-4433'),
       ('A_POSITIVE', '1988-03-23', NULL, 'Dinesh', 'Male', 'Kartik', '555-3301'),
       ('B_POSITIVE', '1995-07-09', NULL, 'Maya', 'Female', 'Patel', '555-1202'),
       ('AB_POSITIVE', '1982-11-18', NULL, 'Rohan', 'Male', 'Shah', '555-9981'),
       ('O_NEGATIVE', '2001-01-30', NULL, 'Sara', 'Female', 'Khan', '555-6634');


-- Insert doctors
INSERT INTO doctor (name, specialization, email)
VALUES ('Dr. Rakesh Mehta', 'Cardiology', 'rakesh.mehta@example.com'),
       ('Dr. Sneha Kapoor', 'Dermatology', 'sneha.kapoor@example.com'),
       ('Dr. Arjun Nair', 'Orthopedics', 'arjun.nair@example.com');


-- Insert appointments
INSERT INTO appointment (doctor_id, patient_id, appointment_time, reason)
VALUES (1, 1, '2026-11-02 10:30:00', 'General Checkup'),
       (2, 2, '2026-11-02 11:00:00', 'Skin Rash'),
       (3, 3, '2026-11-03 09:45:00', 'Knee Pain'),
       (1, 4, '2026-11-03 14:00:00', 'Follow-up Visit'),
       (2, 5, '2026-11-04 16:15:00', 'Allergy Treatment'),
       (1, 1, '2026-11-05 08:30:00', 'Consultation');