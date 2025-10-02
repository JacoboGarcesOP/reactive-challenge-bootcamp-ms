package co.com.bancolombia.events.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.Queue;

@Configuration
public class RabbitMQConfig {
  @Value("${adapter.rabbit.queue}")
  private String queue;
  @Value("${adapter.rabbit.exchange}")
  private String exchange;
  @Value("${adapter.rabbit.routingkey}")
  private String routingKey;

  @Bean
  public Queue queue() {
    return new Queue(queue, true);
  }

  @Bean
  public TopicExchange topicExchange() {
    return new TopicExchange(exchange);
  }

  @Bean
  public Binding binding(Queue queue, TopicExchange topicExchange) {
    return BindingBuilder.bind(queue).to(topicExchange).with(routingKey);
  }

  @Bean
  public MessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(messageConverter());
    return rabbitTemplate;
  }
}