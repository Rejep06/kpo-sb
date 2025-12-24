package com.gozon.payments.api;

import com.gozon.payments.application.AccountCommandService;
import com.gozon.payments.application.AccountQueryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountCommandService commandService;
    private final AccountQueryService queryService;

    public AccountController(AccountCommandService commandService, AccountQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createAccount(@RequestHeader("X-User-Id") String userId) {
        commandService.createAccount(userId);
    }

    @PostMapping("/topup")
    public void topUp(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody TopUpRequest request
    ) {
        commandService.topUp(userId, request.amount());
    }

    @GetMapping("/balance")
    public BalanceResponse balance(@RequestHeader("X-User-Id") String userId) {
        return new BalanceResponse(queryService.getBalance(userId));
    }
}
