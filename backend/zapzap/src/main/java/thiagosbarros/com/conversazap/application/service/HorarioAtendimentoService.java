package thiagosbarros.com.conversazap.application.service;


import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class HorarioAtendimentoService {

    public boolean estaDentroDoHorario(LocalDateTime agora) {
        agora = agora.plusHours(-6);
        DayOfWeek dia = agora.getDayOfWeek();
        LocalTime hora = agora.toLocalTime();

        // Segunda a sexta 08:00 - 18:00
        if(dia.getValue() >= DayOfWeek.MONDAY.getValue()
                && dia.getValue() <= DayOfWeek.FRIDAY.getValue()){
            return !hora.isBefore(LocalTime.of(8, 0))
                    && !hora.isAfter(LocalTime.of(18, 0));
        }

        // Caso seja no Sabado 8 as 12
        if(dia == DayOfWeek.SATURDAY){
            return !hora.isBefore(LocalTime.of(8, 0))
                    && !hora.isAfter(LocalTime.of(12, 0));
        }

        return false;
    }
}
