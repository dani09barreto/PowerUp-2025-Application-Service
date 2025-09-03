package co.com.pragma.bootcamp.application.usecase.listapplications;

import co.com.pragma.bootcamp.application.model.applicationstatus.enums.ApplicationStatusEnum;
import co.com.pragma.bootcamp.application.model.applicationstatus.gateways.IApplicationStatusRepository;
import co.com.pragma.bootcamp.application.model.loanapplication.gateways.ILoanApplicationRepository;
import co.com.pragma.bootcamp.application.model.loantype.gateways.ILoanTypeRepository;
import co.com.pragma.bootcamp.application.model.user.gateways.IUserRepository;
import co.com.pragma.bootcamp.application.usecase.listapplications.dto.ApplicationListDto;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RequiredArgsConstructor
public class ListApplicationsUseCase implements IListApplicationsUseCase{

    private final ILoanApplicationRepository loanApplicationRepository;
    private final IApplicationStatusRepository applicationStatusRepository;
    private final ILoanTypeRepository loanTypeRepository;
    private final IUserRepository userRepository;


    @Override
    public Flux<ApplicationListDto> listApplicationsByApplicationStatus(List<String> applicationStatusList, Integer page, Integer size) {

        return loanApplicationRepository.findLoanApplicationsByApplicationStatusNameIn(applicationStatusList, page, size)
                .flatMap(loanApplication ->
                        loanTypeRepository.findById(loanApplication.getLoanType().getId())
                                .zipWith(applicationStatusRepository.findById(loanApplication.getApplicationStatus().getId()))
                                .zipWhen(tuple -> userRepository.findById(loanApplication.getUser().getId())
                                        .map(user -> Tuples.of(tuple.getT1(), tuple.getT2(), user))
                                )
                                .map(tuple -> ApplicationListDto.builder()
                                        .amount(loanApplication.getAmount())
                                        .termMonths(loanApplication.getTermMonths())
                                        .firstName(tuple.getT2().getT3().getFirstName())
                                        .loadTypeName(tuple.getT1().getT1().getName())
                                        .applicationStatusName(tuple.getT1().getT2().getName())
                                        .interestRate(loanApplication.getAnnualRate())
                                        .email(tuple.getT2().getT3().getEmail())
                                        .baseSalary(tuple.getT2().getT3().getBaseSalary())
                                        .monthlyPayment(
                                                tuple.getT1().getT2().getName().equals(ApplicationStatusEnum.APPROVED.name())
                                                        ? calculateMonthlyPayment(
                                                        loanApplication.getAmount(),
                                                        loanApplication.getAnnualRate(),
                                                        loanApplication.getTermMonths()
                                                )
                                                        : BigDecimal.ZERO
                                        )
                                        .build())
                );

    }

    private BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal annualRate, int months) {
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return amount.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
        }
        double r = monthlyRate.doubleValue();
        double n = months;
        double M = amount.doubleValue();

        double value = (M * r) / (1 - Math.pow(1 + r, -n));
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
}
