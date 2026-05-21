package io.github.fengyanglin09.spalibdemo.rulesEngineDemo;


import io.github.fengyanglin09.spalibdemo.rulesEngineDemo.model.DiscountRequest;
import io.github.fengyanglin09.spalibdemo.rulesEngineDemo.model.DiscountResult;
import lombok.RequiredArgsConstructor;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DroolsDiscountService {

    private final KieContainer kieContainer;

    public DiscountResult calculateDiscount(DiscountRequest request) {
        DiscountResult result = new DiscountResult();

        KieSession kieSession = kieContainer.newKieSession("discountKSession");

        try {
            kieSession.insert(request);
            kieSession.insert(result);
            kieSession.fireAllRules();
            return result;
        } finally {
            kieSession.dispose();
        }

    }

}
