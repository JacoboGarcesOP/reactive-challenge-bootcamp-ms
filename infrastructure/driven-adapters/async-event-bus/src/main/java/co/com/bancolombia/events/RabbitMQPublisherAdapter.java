package co.com.bancolombia.events;

import co.com.bancolombia.model.bootcamp.Bootcamp;
import co.com.bancolombia.model.bootcamp.gateway.PublisherGateway;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class RabbitMQPublisherAdapter implements PublisherGateway {
  private final String MESSAGE_SENDED_MESSAGE = "Bootcamp has been published successfully";
  @Value("${adapter.rabbit.exchange}")
  private String exchange;
  @Value("${adapter.rabbit.routingkey}")
  private String routingKey;

  private final RabbitTemplate rabbitTemplate;

  @Override
  public void publish(Bootcamp bootcamp) {
    BootcampMessage message = new BootcampMessage(
      bootcamp.getId().getValue(),
      bootcamp.getName().getValue(),
      bootcamp.getDescription().getValue(),
      Date.from(bootcamp.getLaunchDate().getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
      bootcamp.getDuration().getValue(),
      bootcamp.getCapacities().size(),
      bootcamp.getCapacities().stream().mapToInt(capacity -> capacity.getTechnologies().size()).sum()
    );
    rabbitTemplate.convertAndSend(exchange, routingKey, message);
    log.info(MESSAGE_SENDED_MESSAGE);
  }
}
