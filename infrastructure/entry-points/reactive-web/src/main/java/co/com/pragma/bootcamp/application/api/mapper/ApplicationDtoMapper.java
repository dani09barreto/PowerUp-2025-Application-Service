package co.com.pragma.bootcamp.application.api.mapper;

import co.com.pragma.bootcamp.application.api.dto.RegisterApplicationRequest;
import co.com.pragma.bootcamp.application.api.dto.RegisterApplicationResponse;
import co.com.pragma.bootcamp.application.model.loanapplication.LoanApplication;
import co.com.pragma.bootcamp.application.model.loantype.LoanType;
import co.com.pragma.bootcamp.application.model.user.User;

public class ApplicationDtoMapper {

    public static LoanApplication toLoanApplication(RegisterApplicationRequest registerApplicationRequest) {
        return LoanApplication.builder()
                .user(User.builder().identificationNumber(registerApplicationRequest.documentNumber()).build())
                .loanType(LoanType.builder().id(registerApplicationRequest.loanTypeId()).build())
                .amount(registerApplicationRequest.amount())
                .termMonths(registerApplicationRequest.termMonths())
                .build();
    }

    public static RegisterApplicationResponse toRegisterApplicationResponse(LoanApplication loanApplication) {
        return new RegisterApplicationResponse(
                loanApplication.getId(),
                loanApplication.getUser() != null ? loanApplication.getUser().getId() : null,
                loanApplication.getLoanType() != null ? loanApplication.getLoanType().getId() : null,
                loanApplication.getApplicationStatus() != null ? loanApplication.getApplicationStatus().getId() : null,
                loanApplication.getTermMonths(),
                loanApplication.getAmount()
        );
    }
}
