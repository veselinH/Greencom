package bg.greencom.greencomwebapp.util;

import bg.greencom.greencomwebapp.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DBInit implements CommandLineRunner {

    private final AdditionalPackageService additionalPackageService;
    private final InternetExtraService internetExtraService;
    private final MobileExtraService mobileExtraService;
    private final UserRoleService userRoleService;
    private final UserService userService;
    private final TelevisionTypeService televisionTypeService;
    private final InternetTypeService internetTypeService;

    public DBInit(AdditionalPackageService additionalPackageService, InternetExtraService internetExtraService, MobileExtraService mobileExtraService, UserRoleService userRoleService, UserService userService, TelevisionTypeService televisionTypeService, InternetTypeService internetTypeService) {
        this.additionalPackageService = additionalPackageService;
        this.internetExtraService = internetExtraService;
        this.mobileExtraService = mobileExtraService;
        this.userRoleService = userRoleService;
        this.userService = userService;
        this.televisionTypeService = televisionTypeService;
        this.internetTypeService = internetTypeService;
    }

    @Override
    public void run(String... args) throws Exception {
        additionalPackageService.initialize();
        internetExtraService.initialize();
        mobileExtraService.initialize();
        userRoleService.initialize();
        userService.initialize();
        televisionTypeService.initialize();
        internetTypeService.initialize();
    }
}
