package com.example.demo.student;

import com.example.demo.student.exception.BadRequestException;
import com.example.demo.student.exception.StudentNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@AllArgsConstructor
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public void addStudent(Student student) {
        Boolean existsEmail = studentRepository
                .selectExistsEmail(student.getEmail());
        if (existsEmail) {
            throw new BadRequestException(
                    "Email " + student.getEmail() + " taken");
        }

        studentRepository.save(student);
    }

    public void deleteStudent(Long studentId) {
        if(!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException(
                    "Student with id " + studentId + " does not exists");
        }
        studentRepository.deleteById(studentId);
    }
    
    
    // We have to annotate it with Transactional so we prevent multiple updates to the same records at the same time (using implicit locks)
    @Transactional
    public void updateStudent(Long studentId
                             String name,
                             String email
                             ){
        
        Student student = studentRepository.updateById(studentId)
                            .orElseThrow(() -> new IllegalStateException("Student with ID " + studentId + " Doesn't exist.") );
        
        if(
            name != null &&
            name.length > 0 &&
            !Object.equals(student.getName(),name)
            ) 
        {
            student.setName(name);
        }
        
        if(
            email != null &&
            email.length > 0 &&
            !Object.equals(student.getEmail(),email)
            ) 
        {            
            Optional<Student> optStudent = studentRepository.findByEmail(email);
            if( optStudent.isPresent()){
                throw new IllegalStateException("Email is Already Taken");
            }
            student.setEmail(email);       
        }
        
    }
}
