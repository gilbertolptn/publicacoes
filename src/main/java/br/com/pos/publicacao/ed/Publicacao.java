package br.com.pos.publicacao.ed;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;

@Entity
public class Publicacao implements Serializable
{

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   @Column(name = "id", updatable = false, nullable = false)
   private Long id;
   @Version
   @Column(name = "version")
   private int version;

   @Column
   private String status;

   @Column
   private Integer ano;

   @Column
   private String descricao;

   @OneToMany
   private List<Autor> autores;

   @ManyToOne
   private TipoPublicacao tipoPublicao;

   public Long getId()
   {
      return this.id;
   }

   public void setId(final Long id)
   {
      this.id = id;
   }

   public int getVersion()
   {
      return this.version;
   }

   public void setVersion(final int version)
   {
      this.version = version;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
      {
         return true;
      }
      if (!(obj instanceof Publicacao))
      {
         return false;
      }
      Publicacao other = (Publicacao) obj;
      if (id != null)
      {
         if (!id.equals(other.id))
         {
            return false;
         }
      }
      return true;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      return result;
   }

   public String getStatus()
   {
      return status;
   }

   public void setStatus(String status)
   {
      this.status = status;
   }

   public Integer getAno()
   {
      return ano;
   }

   public void setAno(Integer ano)
   {
      this.ano = ano;
   }

   public String getDescricao()
   {
      return descricao;
   }

   public void setDescricao(String descricao)
   {
      this.descricao = descricao;
   }

   public List<Autor> getAutores()
   {
      return autores;
   }

   public void setAutores(List<Autor> autores)
   {
      this.autores = autores;
   }

   public TipoPublicacao getTipoPublicao()
   {
      return tipoPublicao;
   }

   public void setTipoPublicao(TipoPublicacao tipoPublicao)
   {
      this.tipoPublicao = tipoPublicao;
   }

   @Override
   public String toString()
   {
      String result = getClass().getSimpleName() + " ";
      if (status != null && !status.trim().isEmpty())
         result += "status: " + status;
      result += ", ano: " + ano;
      if (descricao != null && !descricao.trim().isEmpty())
         result += ", descricao: " + descricao;
      return result;
   }
}