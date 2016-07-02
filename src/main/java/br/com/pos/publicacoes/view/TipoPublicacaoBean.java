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

import br.com.pos.publicacao.ed.TipoPublicacao;

/**
 * Backing bean for TipoPublicacao entities.
 * <p/>
 * This class provides CRUD functionality for all TipoPublicacao entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class TipoPublicacaoBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving TipoPublicacao entities
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

   private TipoPublicacao tipoPublicacao;

   public TipoPublicacao getTipoPublicacao()
   {
      return this.tipoPublicacao;
   }

   public void setTipoPublicacao(TipoPublicacao tipoPublicacao)
   {
      this.tipoPublicacao = tipoPublicacao;
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
         this.tipoPublicacao = this.example;
      }
      else
      {
         this.tipoPublicacao = findById(getId());
      }
   }

   public TipoPublicacao findById(Long id)
   {

      return this.entityManager.find(TipoPublicacao.class, id);
   }

   /*
    * Support updating and deleting TipoPublicacao entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.tipoPublicacao);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.tipoPublicacao);
            return "view?faces-redirect=true&id=" + this.tipoPublicacao.getId();
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
         TipoPublicacao deletableEntity = findById(getId());

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
    * Support searching TipoPublicacao entities with pagination
    */

   private int page;
   private long count;
   private List<TipoPublicacao> pageItems;

   private TipoPublicacao example = new TipoPublicacao();

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

   public TipoPublicacao getExample()
   {
      return this.example;
   }

   public void setExample(TipoPublicacao example)
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
      Root<TipoPublicacao> root = countCriteria.from(TipoPublicacao.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<TipoPublicacao> criteria = builder.createQuery(TipoPublicacao.class);
      root = criteria.from(TipoPublicacao.class);
      TypedQuery<TipoPublicacao> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<TipoPublicacao> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      String nome = this.example.getNome();
      if (nome != null && !"".equals(nome))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("nome")), '%' + nome.toLowerCase() + '%'));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<TipoPublicacao> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back TipoPublicacao entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<TipoPublicacao> getAll()
   {

      CriteriaQuery<TipoPublicacao> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(TipoPublicacao.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(TipoPublicacao.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final TipoPublicacaoBean ejbProxy = this.sessionContext.getBusinessObject(TipoPublicacaoBean.class);

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

            return String.valueOf(((TipoPublicacao) value).getId());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private TipoPublicacao add = new TipoPublicacao();

   public TipoPublicacao getAdd()
   {
      return this.add;
   }

   public TipoPublicacao getAdded()
   {
      TipoPublicacao added = this.add;
      this.add = new TipoPublicacao();
      return added;
   }
}
