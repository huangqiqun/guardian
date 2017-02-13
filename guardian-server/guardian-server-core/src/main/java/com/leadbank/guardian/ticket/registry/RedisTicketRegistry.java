package com.leadbank.guardian.ticket.registry;

import com.leadbank.guardian.constant.RedisKey;
import com.leadbank.guardian.monitor.TicketRegistryState;
import com.leadbank.guardian.ticket.ServiceTicket;
import com.leadbank.guardian.ticket.Ticket;
import com.leadbank.guardian.ticket.TicketGrantingTicket;
import com.leadbank.guardian.util.ObjectSerializeUtil;
import org.springframework.util.Assert;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class RedisTicketRegistry extends AbstractTicketRegistry implements TicketRegistryState {

    private JedisCluster jedisCluster;

    public RedisTicketRegistry() {
    }

    public void addTicket(Ticket ticket) {
        Assert.notNull(ticket, "ticket cannot be null");

        if (log.isDebugEnabled()) {
            log.debug("Added ticket [" + ticket.getId() + "] to registry.");
        }

        try {
            String s = ObjectSerializeUtil.serialize(ticket);
            this.jedisCluster.hset(RedisKey.TICKETREGISTRY, ticket.getId(), s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Ticket getTicket(final String ticketId) {
        if (ticketId == null) {
            return null;
        }

        if (log.isDebugEnabled()) {
            log.debug("Attempting to retrieve ticket [" + ticketId + "]");
        }

        String s = this.jedisCluster.hget(RedisKey.TICKETREGISTRY, ticketId);
        Ticket ticket = null;
        try {
            ticket = (Ticket) ObjectSerializeUtil.deserialize(s);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (ticket != null) {
            log.debug("Ticket [" + ticketId + "] found in registry.");
        }

        return ticket;
    }

    public boolean deleteTicket(final String ticketId) {
        if (ticketId == null) {
            return false;
        }

        if (log.isDebugEnabled()) {
            log.debug("Removing ticket [" + ticketId + "] from registry");
        }

        Long result = this.jedisCluster.hdel(RedisKey.TICKETREGISTRY, ticketId);
        return (result != 0);
    }

    public Collection<Ticket> getTickets() {
        List<String> vals = this.jedisCluster.hvals(RedisKey.TICKETREGISTRY);
        Collection<Ticket> list = new ArrayList<Ticket>();
        for (String val : vals) {
            Ticket ticket = null;
            try {
                ticket = (Ticket) ObjectSerializeUtil.deserialize(val);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            list.add(ticket);
        }

        return list;
    }

    public int sessionCount() {
        List<String> vals = this.jedisCluster.hvals(RedisKey.TICKETREGISTRY);
        List<Ticket> tickets = new ArrayList<Ticket>();
        for (String val : vals) {
            /*Ticket ticket = gson.fromJson(val, Ticket.class);
            tickets.add(ticket);*/
        }

        int count = 0;
        for (Ticket ticket : tickets) {
            if (ticket instanceof TicketGrantingTicket) {
                count++;
            }
        }
        return count;
    }

    public int serviceTicketCount() {
        List<String> vals = this.jedisCluster.hvals(RedisKey.TICKETREGISTRY);
        List<Ticket> tickets = new ArrayList<Ticket>();
        for (String val : vals) {
            /*Ticket ticket = gson.fromJson(val, Ticket.class);
            tickets.add(ticket);*/
        }

        int count = 0;
        for (Ticket ticket : tickets) {
            if (ticket instanceof ServiceTicket) {
                count++;
            }
        }
        return count;
    }

    public void setJedisCluster(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

}
