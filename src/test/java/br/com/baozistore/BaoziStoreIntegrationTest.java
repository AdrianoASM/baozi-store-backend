package br.com.baozistore;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BaoziStoreIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    private long criar(String recurso, String json) throws Exception {
        String resposta = mvc.perform(post("/" + recurso).contentType(APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andExpect(header().exists("Location"))
                .andReturn().getResponse().getContentAsString();
        return mapper.readTree(resposta).get("id").asLong();
    }

    @Test void cicloCompletoEIntegridadeDosVinculos() throws Exception {
        long cliente = criar("clientes", "{\"nome\":\"Cliente de exemplo\",\"clienteDesde\":\"2026-11-17\"}");
        long produto = criar("produtos", "{\"nome\":\"Baozi de porco\",\"preco\":8.50,\"estoque\":true}");
        long pedido = criar("pedidos", "{\"clienteId\":" + cliente + ",\"produtoId\":" + produto + ",\"quantidade\":3}");
        for (String recurso : new String[]{"clientes", "produtos", "pedidos"}) {
            mvc.perform(get("/" + recurso)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        }
        mvc.perform(get("/clientes/" + cliente)).andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Cliente de exemplo"));
        mvc.perform(get("/produtos/" + produto)).andExpect(status().isOk()).andExpect(jsonPath("$.preco").value(8.50));
        mvc.perform(get("/pedidos/" + pedido)).andExpect(status().isOk())
                .andExpect(jsonPath("$.cliente.id").value(cliente)).andExpect(jsonPath("$.produto.id").value(produto))
                .andExpect(jsonPath("$.quantidade").value(3));
        mvc.perform(delete("/clientes/" + cliente)).andExpect(status().isConflict());
        mvc.perform(delete("/produtos/" + produto)).andExpect(status().isConflict());
        mvc.perform(delete("/pedidos/" + pedido)).andExpect(status().isNoContent());
        mvc.perform(delete("/clientes/" + cliente)).andExpect(status().isNoContent());
        mvc.perform(delete("/produtos/" + produto)).andExpect(status().isNoContent());
        mvc.perform(get("/pedidos/" + pedido)).andExpect(status().isNotFound());
    }

    @Test void rejeitaDadosInvalidosEReferenciasInexistentes() throws Exception {
        mvc.perform(post("/clientes").contentType(APPLICATION_JSON).content("{\"nome\":\"\",\"clienteDesde\":\"2026-11-17\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/produtos").contentType(APPLICATION_JSON).content("{\"nome\":\"Baozi\",\"preco\":-1,\"estoque\":true}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/pedidos").contentType(APPLICATION_JSON).content("{\"clienteId\":999999,\"produtoId\":999999,\"quantidade\":1}"))
                .andExpect(status().isNotFound());
        mvc.perform(post("/pedidos").contentType(APPLICATION_JSON).content("{\"clienteId\":1,\"produtoId\":1,\"quantidade\":0}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/pedidos")).andExpect(jsonPath("$.length()").value(0));
    }

    @Test void rejeitaPedidoDeProdutoIndisponivel() throws Exception {
        long cliente = criar("clientes", "{\"nome\":\"Teste\",\"clienteDesde\":\"2026-11-17\"}");
        long produto = criar("produtos", "{\"nome\":\"Baozi\",\"preco\":8.50,\"estoque\":false}");
        mvc.perform(post("/pedidos").contentType(APPLICATION_JSON)
                .content("{\"clienteId\":" + cliente + ",\"produtoId\":" + produto + ",\"quantidade\":1}"))
                .andExpect(status().isConflict());
        mvc.perform(get("/pedidos")).andExpect(jsonPath("$.length()").value(0));
    }
}
