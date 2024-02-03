package com.school.sba.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.Enum.ClassStatus;
import com.school.sba.Enum.UserRole;
import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Scheduleld;
import com.school.sba.entity.Subject;
import com.school.sba.entity.User;
import com.school.sba.exception.IllegalArgumentException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.ClassHourRepository;
import com.school.sba.repository.SubjectRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestDTO.ClassHourDTO;
import com.school.sba.requestDTO.ExcelRequestDTO;
import com.school.sba.responseDTO.ClassHourResponce;
import com.school.sba.service.ClassHourService;
import com.school.sba.util.ResponseStructure;

@Service
public class ClassHourServiceImpl implements ClassHourService {
	@Autowired
	private AcademicProgramRepository academicProgramRepository;
	@Autowired
	private ClassHourRepository classHourRepository;
	@Autowired
	private SubjectRepository subjectRepository;
	@Autowired
	private UserRepository userRepository;

	@Override
	public ResponseEntity<String> generateClassHourForAcademicProgram(int programId) {
		//		classHourRepository.deleteAll();
		return academicProgramRepository.findById(programId).map(academicProgram->{
			Scheduleld scheduleld = academicProgram.getSchool().getScheduleld();
			if(scheduleld != null) {
				int classHoursPerDay = scheduleld.getClassHoursPerDay();
				int classHourLength = (int)scheduleld.getClassHourLengthInMin().toMinutes();

				//				gives present day
				//				LocalDateTime currentTime = LocalDateTime.now().with(scheduleld.getOpensAt());
				// Find the next Monday from the current date
				LocalDateTime currentTime = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).with(scheduleld.getOpensAt());

				// Pre-calculate time renges for clarity
				LocalTime lunchTimeStart = scheduleld.getLunchTime();
				LocalTime lunchTimeEnd = lunchTimeStart.plusMinutes(scheduleld.getLunchLengthInMin().toMinutes());
				LocalTime breakTimeStart = scheduleld.getBreakTime();
				LocalTime breakTimeEnd = breakTimeStart.plusMinutes(scheduleld.getBreakLengthInMin().toMinutes());

				for(int day=1;day<=6;day++) {
					for(int hour=0;hour<classHoursPerDay;hour++) {
						ClassHour classHour = new ClassHour();
						//Assign a value to roomNo (assuming it's madatory)
						classHour.setRoomNo(100);

						if (currentTime.toLocalTime().equals(breakTimeStart) || 
								(currentTime.toLocalTime().isAfter(breakTimeStart) && currentTime.toLocalTime().isBefore(breakTimeEnd))) {

							classHour.setBeginsAt(currentTime);
							classHour.setEndsAt(currentTime.plusMinutes(scheduleld.getBreakLengthInMin().toMinutes()));
							classHour.setClassStatus(ClassStatus.BREAK_TIMINGS);
							currentTime = classHour.getEndsAt();

						} else if (currentTime.toLocalTime().equals(lunchTimeStart) || 
								(currentTime.toLocalTime().isAfter(lunchTimeStart) && currentTime.toLocalTime().isBefore(lunchTimeEnd))) {

							classHour.setBeginsAt(currentTime);
							classHour.setEndsAt(currentTime.plusMinutes(scheduleld.getLunchLengthInMin().toMinutes()));
							classHour.setClassStatus(ClassStatus.LUNCH_TIMINGS);
							currentTime = classHour.getEndsAt();

						} else {

							LocalDateTime beginsAt = currentTime;
							LocalDateTime endsAt = beginsAt.plusMinutes(classHourLength);

							classHour.setBeginsAt(beginsAt);
							classHour.setEndsAt(endsAt);
							classHour.setClassStatus(ClassStatus.NOT_SCHEDULED);

							currentTime = endsAt;
						}

						classHour.setAcademicProgram(academicProgram);
						classHourRepository.save(classHour);
					}
					currentTime = currentTime.plusDays(1).with(scheduleld.getOpensAt());
				}
				return ResponseEntity.status(HttpStatus.CREATED)
						.body("Class Hour generated for the current week successfully");
			}else {
				throw new IllegalArgumentException("School doesn't have schedule");
			}
		}).orElseThrow(() -> new IllegalArgumentException("Invalid Program"));
	}
	@Override
	public ResponseEntity<String> autoGenerateClassHour(int programId) {

		if(classHourRepository.findAll()==null)throw new IllegalArgumentException("Create Previous weeks ClassHours");

		LocalDateTime day = classHourRepository.findTopByOrderByEndsAtDesc().getBeginsAt();
		LocalDateTime startOfMonday = day.with(TemporalAdjusters.next(DayOfWeek.MONDAY));

		AcademicProgram academicProgram = academicProgramRepository.findById(programId).get();
		Scheduleld scheduleld = academicProgram.getSchool().getScheduleld();
		LocalTime time = scheduleld.getOpensAt();
		startOfMonday=startOfMonday.with(time);

		if(!LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).equals(startOfMonday.toLocalDate()) ||
				!LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).equals(startOfMonday.toLocalDate()))
			throw new IllegalArgumentException("ClassHours are generated for next week");

		List<ClassHour> list = classHourRepository.findByAcademicProgramAndBeginsAtBetween(academicProgram, 
				day.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)), 
				day.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)));
		int i =0;
		;
		for(ClassHour classHour : list){
			ClassHour nextClassHour = new ClassHour();

			nextClassHour.setAcademicProgram(classHour.getAcademicProgram());
			nextClassHour.setRoomNo(classHour.getRoomNo());
			nextClassHour.setSubject(classHour.getSubject());
			nextClassHour.setUser(classHour.getUser());
			nextClassHour.setClassStatus(classHour.getClassStatus());

			nextClassHour.setBeginsAt(startOfMonday);
			if(nextClassHour.getClassStatus().equals(ClassStatus.BREAK_TIMINGS)) {
				nextClassHour.setEndsAt(startOfMonday.plusMinutes(scheduleld.getBreakLengthInMin().toMinutes()));
				startOfMonday = startOfMonday.plusMinutes(scheduleld.getBreakLengthInMin().toMinutes());
			}
			else if(nextClassHour.getClassStatus().equals(ClassStatus.LUNCH_TIMINGS)) {
				nextClassHour.setEndsAt(startOfMonday.plusMinutes(scheduleld.getLunchLengthInMin().toMinutes()));
				startOfMonday = startOfMonday.plusMinutes(scheduleld.getLunchLengthInMin().toMinutes());
			}
			else {
				nextClassHour.setEndsAt(startOfMonday.plusMinutes(scheduleld.getClassHourLengthInMin().toMinutes()));
				startOfMonday = startOfMonday.plusMinutes(scheduleld.getClassHourLengthInMin().toMinutes());
			}

			if(!nextClassHour.getClassStatus().equals(ClassStatus.NOT_SCHEDULED) && 
					nextClassHour.getClassStatus().equals(ClassStatus.BREAK_TIMINGS) &&
					nextClassHour.getClassStatus().equals(ClassStatus.LUNCH_TIMINGS))
				nextClassHour.setClassStatus(ClassStatus.UPCOMING);

			i++;
			classHourRepository.save(nextClassHour);

			if(i%8==0) {
				startOfMonday = startOfMonday.plusDays(1).with(scheduleld.getOpensAt());
			}
		}

		return new ResponseEntity<String>("Sheduled Successfully", HttpStatus.CREATED);
	}
	@Override
	public ResponseEntity<ResponseStructure<List<ClassHourResponce>>> updateClassHour(List<ClassHourDTO> classHourDtoList) {
		List<ClassHourResponce> updatedClassHourResponses = new ArrayList<>();

		classHourDtoList.forEach(classHourDTO -> {
			ClassHour existingClassHour = classHourRepository.findById(classHourDTO.getClassHourId()).get();
			Subject subject=subjectRepository.findById(classHourDTO.getSubjectId()).get();
			User teacher=userRepository.findById(classHourDTO.getTeacherId()).get();

			if(existingClassHour != null && subject != null && teacher != null && teacher.getUserRole().equals(UserRole.TEACHER)&& 
					existingClassHour.getClassStatus().equals(ClassStatus.NOT_SCHEDULED)) {

				if((teacher.getSubject()).equals(subject))
					existingClassHour.setSubject(subject);
				else
					throw new IllegalArgumentException("The Teacher is Not Teaching That Subject");
				existingClassHour.setUser(teacher);
				existingClassHour.setRoomNo(classHourDTO.getRoomNo());
				LocalDateTime currentTime = LocalDateTime.now();

				if (existingClassHour.getBeginsAt().isBefore(currentTime) && existingClassHour.getEndsAt().isAfter(currentTime)) {
					existingClassHour.setClassStatus(ClassStatus.ONGOING);
				} else if (existingClassHour.getEndsAt().isBefore(currentTime)) {
					existingClassHour.setClassStatus(ClassStatus.COMPLETED);
				} else {
					existingClassHour.setClassStatus(ClassStatus.UPCOMING);
				}

				existingClassHour=classHourRepository.save(existingClassHour);

				ClassHourResponce classHourResponse = new ClassHourResponce();
				classHourResponse.setBeginsAt(existingClassHour.getBeginsAt());
				classHourResponse.setEndsAt(existingClassHour.getEndsAt());
				classHourResponse.setClassstatus(existingClassHour.getClassStatus());
				classHourResponse.setRoomNo(existingClassHour.getRoomNo());
				updatedClassHourResponses.add(classHourResponse);

			} 
			else {
				throw new IllegalArgumentException("Invalid Teacher Id or adding class between break/lunch time");
			}
		});
		ResponseStructure<List<ClassHourResponce>> responseStructure = new ResponseStructure<>();
		responseStructure.setStatus(HttpStatus.CREATED.value());
		responseStructure.setMessage("ClassHours updated successfully!!!!");
		responseStructure.setData(updatedClassHourResponses);

		return new ResponseEntity<ResponseStructure<List<ClassHourResponce>>>(responseStructure, HttpStatus.CREATED);
	}
	@Override
	public ResponseEntity<String> acceptExcelSheet(int programId, ExcelRequestDTO excelRequestDTO) {

		AcademicProgram academicProgram = academicProgramRepository.findById(programId).get();

		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();

		LocalDateTime from = excelRequestDTO.getFromDate().atStartOfDay();
		LocalDateTime to = excelRequestDTO.getToDate().atStartOfDay().plusDays(1);

		List<ClassHour> list = classHourRepository.findByAcademicProgramAndBeginsAtBetween(academicProgram, from, to);

		int rowNumber = 0;
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("Begin Date");
		header.createCell(1).setCellValue("Begin Time");
		header.createCell(2).setCellValue("End Date");
		header.createCell(3).setCellValue("End Time");
		header.createCell(4).setCellValue("Subject");
		header.createCell(5).setCellValue("Teacher");
		header.createCell(6).setCellValue("Room No");

		DateTimeFormatter time = DateTimeFormatter.ofPattern("hh:MM");
		DateTimeFormatter date = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		for(ClassHour classHour : list) {
			Row row = sheet.createRow(++rowNumber);
			row.createCell(0).setCellValue(date.format(classHour.getBeginsAt()));
			row.createCell(1).setCellValue(time.format(classHour.getBeginsAt()));
			row.createCell(2).setCellValue(date.format(classHour.getEndsAt()));
			row.createCell(3).setCellValue(time.format(classHour.getEndsAt()));
			if(classHour.getSubject()==null && classHour.getUser()==null) {
				row.createCell(4).setCellValue("");
				row.createCell(5).setCellValue("");
			}else {
				row.createCell(4).setCellValue(classHour.getSubject().getSubjectName());
				row.createCell(5).setCellValue(classHour.getUser().getUserName());
			}
			row.createCell(6).setCellValue(classHour.getRoomNo());
		}
		try {
			FileOutputStream fileOut = new FileOutputStream(excelRequestDTO.getFilePath());
			workbook.write(fileOut);
			fileOut.close();
		}catch(Exception e){
			throw new IllegalArgumentException("Faild to store in ExcelSheet");
		}
		return ResponseEntity.ok("Excel Sheet Generated Successfully");
	}

	@Override
	public ResponseEntity<?> writeToExcelSheet(int programId, LocalDate fromDate, LocalDate toDate, MultipartFile file) throws IOException {
		LocalDateTime startDateTime=fromDate.atStartOfDay();
		LocalDateTime endDateTime=toDate.atStartOfDay().plusDays(1);
		AcademicProgram program=academicProgramRepository.findById(programId).get();
		List<ClassHour> listClassHours = classHourRepository.findByAcademicProgramAndBeginsAtBetween(program, startDateTime, endDateTime);
		XSSFWorkbook workbook;

		workbook = new XSSFWorkbook(file.getInputStream());

		DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm"); 
		DateTimeFormatter date = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		workbook.forEach(sheet->{
			int rowNumber=0;
			Row header=sheet.createRow(rowNumber);
			header.createCell(0).setCellValue("Begin Date");
			header.createCell(1).setCellValue("Begin Time");
			header.createCell(2).setCellValue("End Date");
			header.createCell(3).setCellValue("End Time");
			header.createCell(4).setCellValue("Subject");
			header.createCell(5).setCellValue("Teacher");
			header.createCell(6).setCellValue("Room No");
			for(ClassHour classHour : listClassHours) {
				Row row = sheet.createRow(++rowNumber);
				row.createCell(0).setCellValue(date.format(classHour.getBeginsAt()));
				row.createCell(1).setCellValue(time.format(classHour.getBeginsAt()));
				row.createCell(2).setCellValue(date.format(classHour.getEndsAt()));
				row.createCell(3).setCellValue(time.format(classHour.getEndsAt()));
				if(classHour.getSubject()==null&&classHour.getUser()==null) {
					row.createCell(4).setCellValue("");
					row.createCell(5).setCellValue("");	
				}else {
					row.createCell(4).setCellValue(classHour.getSubject().getSubjectName());
					row.createCell(5).setCellValue(classHour.getUser().getUserName());	
				}
				row.createCell(6).setCellValue(classHour.getRoomNo());
			}
		});
		ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
		try {
			workbook.write(outputStream);
			workbook.close();
			byte[] byteData=outputStream.toByteArray();


			return ResponseEntity.ok()
					.header("Content Disposition", "Attachment; filename="+file.getOriginalFilename())
					.contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(byteData);
		} catch (IOException e) {
			throw new IllegalArgumentException("Unable to write File");
		}
	}
}























