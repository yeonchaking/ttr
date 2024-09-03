package com.greentea.ttb.ui.screens.lobby

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.greentea.ttb.data.model.WorkoutRecord
import com.greentea.ttb.data.model.WorkoutSet
import com.greentea.ttb.ui.theme.color3
import com.greentea.ttb.viewmodel.ExerciseViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(viewModel: ExerciseViewModel) {
    val records by viewModel.allWorkoutRecords.collectAsState(emptyList())
    val sets by viewModel.allWorkoutSets.collectAsState(emptyList())

    var selectedYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH) + 1) }
    var expandedYear by remember { mutableStateOf(false) }
    var expandedMonth by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Date?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.weight(0.6f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.padding(5.dp)) {
                    Text(text = "$selectedYear", fontSize = 18.sp, modifier = Modifier
                        .padding(5.dp)
                        .clickable { expandedYear = !expandedYear })
                    DropdownMenu(
                        expanded = expandedYear,
                        onDismissRequest = { expandedYear = false },
                        modifier = Modifier.heightIn(max = 200.dp)
                    ) {
                        val years = (2000..2100).toList()
                        years.forEach { year ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedYear = year
                                    expandedYear = false
                                },
                                text = { Text("${year}년") }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Box(modifier = Modifier.padding(8.dp)) {
                    Text(text = "$selectedMonth", fontSize = 18.sp, modifier = Modifier
                        .padding(8.dp)
                        .clickable { expandedMonth = !expandedMonth })
                    DropdownMenu(
                        expanded = expandedMonth,
                        onDismissRequest = { expandedMonth = false },
                        modifier = Modifier.heightIn(max = 200.dp)
                    ) {
                        val months = (1..12).toList()
                        months.forEach { month ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedMonth = month
                                    expandedMonth = false
                                },
                                text = { Text("${month}월") }
                            )
                        }
                    }
                }
            }

            CalendarView(
                year = selectedYear,
                month = selectedMonth,
                todayRecords = records,
                onDateClick = { date ->
                    selectedDate = date
                }
            )
        }

        Box(modifier = Modifier.weight(0.4f).background(color3).fillMaxWidth().padding(8.dp)) {
            // 하단부를 표시하기 위한 박스 (예: 운동 기록 상세보기 영역)
            if (selectedDate != null) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dateStr = dateFormat.format(selectedDate!!)
                val selectedRecords = records.filter {
                    dateFormat.format(Date(it.startTime)) == dateStr
                }
                val selectedSets = sets.filter { set ->
                    selectedRecords.any { record -> record.id == set.workoutRecordId }
                }

                Column {
                    Text(text = "운동 기록 상세보기", fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))
                    if (selectedRecords.isEmpty()) {
                        Text(text = "운동 기록이 없습니다", fontSize = 16.sp, color = Color.Red)
                    } else {
                        selectedRecords.forEach { record ->
                            Text(text = "운동: ${record.exerciseId}, 총 볼륨: ${record.totalVolume}, 총 시간: ${record.totalTime}")
                        }
                        selectedSets.forEach { set ->
                            Text(text = "세트: 무게: ${set.weight}, 횟수: ${set.reps}, 시간: ${set.duration}")
                        }
                    }
                }
            } else {
                Text(text = "날짜를 선택하세요", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun CalendarView(year: Int, month: Int, todayRecords: List<WorkoutRecord>, onDateClick: (Date) -> Unit) {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month - 1)
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

    Column(modifier = Modifier.fillMaxWidth()) {
        // 요일 헤더 추가
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEach { dayOfWeek ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = dayOfWeek, fontSize = 12.sp)
                }
            }
        }

        for (week in 0..5) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (day in 0..6) {
                    val dayOfMonth = week * 7 + day - firstDayOfWeek + 1
                    if (dayOfMonth in 1..daysInMonth) {
                        val hasWorkout = todayRecords.any { record ->
                            val recordDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(record.startTime))
                            recordDate == "$year-${month.toString().padStart(2, '0')}-${dayOfMonth.toString().padStart(2, '0')}"
                        }
                        val borderColor = if (hasWorkout) Color(0xFF009900) else Color.Gray

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp)
                                .aspectRatio(1f)
                                .border(1.dp, borderColor, RoundedCornerShape(4.dp))
                                .clickable {
                                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                    onDateClick(calendar.time)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = dayOfMonth.toString(), fontSize = 12.sp, modifier = Modifier.padding(4.dp))
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp)
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                        }
                    }
                }
            }
        }
    }
}

fun Brush.Companion.dashedBorderStroke() = Brush.linearGradient(
    colors = listOf(Color.Gray, Color.Gray),
    tileMode = TileMode.Repeated
)
