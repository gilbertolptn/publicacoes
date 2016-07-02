package br.com.pos.publicacoes.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.pos.publicacao.ed.Publicacao;
import br.com.pos.publicacao.ed.TipoPublicacao;

/**
 * Backing bean for Publicacao entities.
 * <p/>
 * This class provides CRUD functionality for all Publicacao entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class PublicacaoBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving Publicacao entities
    */

   private Long id;

   public Long getId()
   {
      return this.id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   private Publicacao publicacao;

   public Publicacao getPublicacao()
   {
      return this.publicacao;
   }

   public void setPublicacao(Publicacao publicacao)
   {
      this.publicacao = publicacao;
   }

   @Inject
   private Conversation conversation;

   @PersistenceContext(unitName = "publicacoes-persistence-unit", type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   public String create()
   {

      this.conversation.begin();
      this.conversation.setTimeout(1800000L);
      return "create?faces-redirect=true";
   }

   public void retrieve()
   {

      if (FacesContext.getCurrentInstance().isPostback())
      {
         return;
      }

      if (this.conversation.isTransient())
      {
         this.conversation.begin();
         this.conversation.setTimeout(1800000L);
      }

      if (this.id == null)
      {
         this.publicacao = this.example;
      }
      else
      {
         this.publicacao = findById(getId());
      }
   }

   public Publicacao findById(Long id)
   {

      return this.entityManager.find(Publicacao.class, id);
   }

   /*
    * Support updating and deleting Publicacao entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.publicacao);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.publicacao);
            return "view?faces-redirect=true&id=" + this.publicacao.getId();
         }
      }
      catch (Exception e)
      {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
         return null;
      }
   }

   public String delete()
   {
      this.conversation.end();

      try
      {
         Publicacao deletableEntity = findById(getId());

         this.entityManager.remove(deletableEntity);
         this.entityManager.flush();
         return "search?faces-redirect=true";
      }
      catch (Exception e)
      {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
         return null;
      }
   }

   /*
    * Support searching Publicacao entities with pagination
    */

   private int page;
   private long count;
   private List<Publicacao> pageItems;

   private Publicacao example = new Publicacao();

   public int getPage()
   {
      return this.page;
   }

   public void setPage(int page)
   {
      this.page = page;
   }

   public int getPageSize()
   {
      return 10;
   }

   public Publicacao getExample()
   {
      return this.example;
   }

   public void setExample(Publicacao example)
   {
      this.example = example;
   }

   public String search()
   {
      this.page = 0;
      return null;
   }

   public void paginate()
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

      // Populate this.count

      CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
      Root<Publicacao> root = countCriteria.from(Publicacao.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<Publicacao> criteria = builder.createQuery(Publicacao.class);
      root = criteria.from(Publicacao.class);
      TypedQuery<Publicacao> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<Publicacao> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      String status = this.example.getStatus();
      if (status != null && !"".equals(status))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("status")), '%' + status.toLowerCase() + '%'));
      }
      Integer ano = this.example.getAno();
      if (ano != null && ano.intValue() != 0)
      {
         predicatesList.add(builder.equal(root.get("ano"), ano));
      }
      String descricao = this.example.getDescricao();
      if (descricao != null && !"".equals(descricao))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("descricao")), '%' + descricao.toLowerCase() + '%'));
      }
      TipoPublicacao tipoPublicao = this.example.getTipoPublicao();
      if (tipoPublicao != null)
      {
         predicatesList.add(builder.equal(root.get("tipoPublicao"), tipoPublicao));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<Publicacao> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back Publicacao entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<Publicacao> getAll()
   {

      CriteriaQuery<Publicacao> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(Publicacao.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(Publicacao.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final PublicacaoBean ejbProxy = this.sessionContext.getBusinessObject(PublicacaoBean.class);

      return new Converter()
      {

         @Override
         public Object getAsObject(FacesContext context,
               UIComponent component, String value)
         {

            return ejbProxy.findById(Long.valueOf(value));
         }

         @Override
         public String getAsString(FacesContext context,
               UIComponent component, Object value)
         {

            if (value == null)
            {
               return "";
            }

            return String.valueOf(((Publicacao) value).getId());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private Publicacao add = new Publicacao();

   public Publicacao getAdd()
   {
      return this.add;
   }

   public Publicacao getAdded()
   {
      Publicacao added = this.add;
      this.add = new Publicacao();
      return added;
   }
}
