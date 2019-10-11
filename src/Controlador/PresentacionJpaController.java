/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Controlador.exceptions.IllegalOrphanException;
import Controlador.exceptions.NonexistentEntityException;
import Controlador.exceptions.PreexistingEntityException;
import ModeloEntidad.Presentacion;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import ModeloEntidad.Producto;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Diego
 */
public class PresentacionJpaController implements Serializable {

    public PresentacionJpaController() {
        this.emf = Persistence.createEntityManagerFactory("Hibernate_Magui_aPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Presentacion presentacion) throws PreexistingEntityException, Exception {
        if (presentacion.getProductoList() == null) {
            presentacion.setProductoList(new ArrayList<Producto>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Producto> attachedProductoList = new ArrayList<Producto>();
            for (Producto productoListProductoToAttach : presentacion.getProductoList()) {
                productoListProductoToAttach = em.getReference(productoListProductoToAttach.getClass(), productoListProductoToAttach.getIdProducto());
                attachedProductoList.add(productoListProductoToAttach);
            }
            presentacion.setProductoList(attachedProductoList);
            em.persist(presentacion);
            for (Producto productoListProducto : presentacion.getProductoList()) {
                Presentacion oldIdPresentacionOfProductoListProducto = productoListProducto.getIdPresentacion();
                productoListProducto.setIdPresentacion(presentacion);
                productoListProducto = em.merge(productoListProducto);
                if (oldIdPresentacionOfProductoListProducto != null) {
                    oldIdPresentacionOfProductoListProducto.getProductoList().remove(productoListProducto);
                    oldIdPresentacionOfProductoListProducto = em.merge(oldIdPresentacionOfProductoListProducto);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPresentacion(presentacion.getIdPresentacion()) != null) {
                throw new PreexistingEntityException("Presentacion " + presentacion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Presentacion presentacion) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Presentacion persistentPresentacion = em.find(Presentacion.class, presentacion.getIdPresentacion());
            List<Producto> productoListOld = persistentPresentacion.getProductoList();
            List<Producto> productoListNew = presentacion.getProductoList();
            List<String> illegalOrphanMessages = null;
            for (Producto productoListOldProducto : productoListOld) {
                if (!productoListNew.contains(productoListOldProducto)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Producto " + productoListOldProducto + " since its idPresentacion field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Producto> attachedProductoListNew = new ArrayList<Producto>();
            for (Producto productoListNewProductoToAttach : productoListNew) {
                productoListNewProductoToAttach = em.getReference(productoListNewProductoToAttach.getClass(), productoListNewProductoToAttach.getIdProducto());
                attachedProductoListNew.add(productoListNewProductoToAttach);
            }
            productoListNew = attachedProductoListNew;
            presentacion.setProductoList(productoListNew);
            presentacion = em.merge(presentacion);
            for (Producto productoListNewProducto : productoListNew) {
                if (!productoListOld.contains(productoListNewProducto)) {
                    Presentacion oldIdPresentacionOfProductoListNewProducto = productoListNewProducto.getIdPresentacion();
                    productoListNewProducto.setIdPresentacion(presentacion);
                    productoListNewProducto = em.merge(productoListNewProducto);
                    if (oldIdPresentacionOfProductoListNewProducto != null && !oldIdPresentacionOfProductoListNewProducto.equals(presentacion)) {
                        oldIdPresentacionOfProductoListNewProducto.getProductoList().remove(productoListNewProducto);
                        oldIdPresentacionOfProductoListNewProducto = em.merge(oldIdPresentacionOfProductoListNewProducto);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = presentacion.getIdPresentacion();
                if (findPresentacion(id) == null) {
                    throw new NonexistentEntityException("The presentacion with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Presentacion presentacion;
            try {
                presentacion = em.getReference(Presentacion.class, id);
                presentacion.getIdPresentacion();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The presentacion with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Producto> productoListOrphanCheck = presentacion.getProductoList();
            for (Producto productoListOrphanCheckProducto : productoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Presentacion (" + presentacion + ") cannot be destroyed since the Producto " + productoListOrphanCheckProducto + " in its productoList field has a non-nullable idPresentacion field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(presentacion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Presentacion> findPresentacionEntities() {
        return findPresentacionEntities(true, -1, -1);
    }

    public List<Presentacion> findPresentacionEntities(int maxResults, int firstResult) {
        return findPresentacionEntities(false, maxResults, firstResult);
    }

    private List<Presentacion> findPresentacionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Presentacion.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Presentacion findPresentacion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Presentacion.class, id);
        } finally {
            em.close();
        }
    }

    public int getPresentacionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Presentacion> rt = cq.from(Presentacion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
