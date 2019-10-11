/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Controlador.exceptions.NonexistentEntityException;
import Controlador.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import ModeloEntidad.Proveedor;
import ModeloEntidad.Presentacion;
import ModeloEntidad.Marca;
import ModeloEntidad.Producto;
import ModeloEntidad.Zona;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Diego
 */
public class ProductoJpaController implements Serializable {

    public ProductoJpaController() {
       this.emf = Persistence.createEntityManagerFactory("Hibernate_Magui_aPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Producto producto) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Proveedor idProveedor = producto.getIdProveedor();
            if (idProveedor != null) {
                idProveedor = em.getReference(idProveedor.getClass(), idProveedor.getIdProveedor());
                producto.setIdProveedor(idProveedor);
            }
            Presentacion idPresentacion = producto.getIdPresentacion();
            if (idPresentacion != null) {
                idPresentacion = em.getReference(idPresentacion.getClass(), idPresentacion.getIdPresentacion());
                producto.setIdPresentacion(idPresentacion);
            }
            Marca idMarca = producto.getIdMarca();
            if (idMarca != null) {
                idMarca = em.getReference(idMarca.getClass(), idMarca.getIdMarca());
                producto.setIdMarca(idMarca);
            }
            Zona idZona = producto.getIdZona();
            if (idZona != null) {
                idZona = em.getReference(idZona.getClass(), idZona.getIdZona());
                producto.setIdZona(idZona);
            }
            em.persist(producto);
            if (idProveedor != null) {
                idProveedor.getProductoList().add(producto);
                idProveedor = em.merge(idProveedor);
            }
            if (idPresentacion != null) {
                idPresentacion.getProductoList().add(producto);
                idPresentacion = em.merge(idPresentacion);
            }
            if (idMarca != null) {
                idMarca.getProductoList().add(producto);
                idMarca = em.merge(idMarca);
            }
            if (idZona != null) {
                idZona.getProductoList().add(producto);
                idZona = em.merge(idZona);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findProducto(producto.getIdProducto()) != null) {
                throw new PreexistingEntityException("Producto " + producto + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Producto producto) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Producto persistentProducto = em.find(Producto.class, producto.getIdProducto());
            Proveedor idProveedorOld = persistentProducto.getIdProveedor();
            Proveedor idProveedorNew = producto.getIdProveedor();
            Presentacion idPresentacionOld = persistentProducto.getIdPresentacion();
            Presentacion idPresentacionNew = producto.getIdPresentacion();
            Marca idMarcaOld = persistentProducto.getIdMarca();
            Marca idMarcaNew = producto.getIdMarca();
            Zona idZonaOld = persistentProducto.getIdZona();
            Zona idZonaNew = producto.getIdZona();
            if (idProveedorNew != null) {
                idProveedorNew = em.getReference(idProveedorNew.getClass(), idProveedorNew.getIdProveedor());
                producto.setIdProveedor(idProveedorNew);
            }
            if (idPresentacionNew != null) {
                idPresentacionNew = em.getReference(idPresentacionNew.getClass(), idPresentacionNew.getIdPresentacion());
                producto.setIdPresentacion(idPresentacionNew);
            }
            if (idMarcaNew != null) {
                idMarcaNew = em.getReference(idMarcaNew.getClass(), idMarcaNew.getIdMarca());
                producto.setIdMarca(idMarcaNew);
            }
            if (idZonaNew != null) {
                idZonaNew = em.getReference(idZonaNew.getClass(), idZonaNew.getIdZona());
                producto.setIdZona(idZonaNew);
            }
            producto = em.merge(producto);
            if (idProveedorOld != null && !idProveedorOld.equals(idProveedorNew)) {
                idProveedorOld.getProductoList().remove(producto);
                idProveedorOld = em.merge(idProveedorOld);
            }
            if (idProveedorNew != null && !idProveedorNew.equals(idProveedorOld)) {
                idProveedorNew.getProductoList().add(producto);
                idProveedorNew = em.merge(idProveedorNew);
            }
            if (idPresentacionOld != null && !idPresentacionOld.equals(idPresentacionNew)) {
                idPresentacionOld.getProductoList().remove(producto);
                idPresentacionOld = em.merge(idPresentacionOld);
            }
            if (idPresentacionNew != null && !idPresentacionNew.equals(idPresentacionOld)) {
                idPresentacionNew.getProductoList().add(producto);
                idPresentacionNew = em.merge(idPresentacionNew);
            }
            if (idMarcaOld != null && !idMarcaOld.equals(idMarcaNew)) {
                idMarcaOld.getProductoList().remove(producto);
                idMarcaOld = em.merge(idMarcaOld);
            }
            if (idMarcaNew != null && !idMarcaNew.equals(idMarcaOld)) {
                idMarcaNew.getProductoList().add(producto);
                idMarcaNew = em.merge(idMarcaNew);
            }
            if (idZonaOld != null && !idZonaOld.equals(idZonaNew)) {
                idZonaOld.getProductoList().remove(producto);
                idZonaOld = em.merge(idZonaOld);
            }
            if (idZonaNew != null && !idZonaNew.equals(idZonaOld)) {
                idZonaNew.getProductoList().add(producto);
                idZonaNew = em.merge(idZonaNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = producto.getIdProducto();
                if (findProducto(id) == null) {
                    throw new NonexistentEntityException("The producto with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Producto producto;
            try {
                producto = em.getReference(Producto.class, id);
                producto.getIdProducto();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The producto with id " + id + " no longer exists.", enfe);
            }
            Proveedor idProveedor = producto.getIdProveedor();
            if (idProveedor != null) {
                idProveedor.getProductoList().remove(producto);
                idProveedor = em.merge(idProveedor);
            }
            Presentacion idPresentacion = producto.getIdPresentacion();
            if (idPresentacion != null) {
                idPresentacion.getProductoList().remove(producto);
                idPresentacion = em.merge(idPresentacion);
            }
            Marca idMarca = producto.getIdMarca();
            if (idMarca != null) {
                idMarca.getProductoList().remove(producto);
                idMarca = em.merge(idMarca);
            }
            Zona idZona = producto.getIdZona();
            if (idZona != null) {
                idZona.getProductoList().remove(producto);
                idZona = em.merge(idZona);
            }
            em.remove(producto);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Producto> findProductoEntities() {
        return findProductoEntities(true, -1, -1);
    }

    public List<Producto> findProductoEntities(int maxResults, int firstResult) {
        return findProductoEntities(false, maxResults, firstResult);
    }

    private List<Producto> findProductoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Producto.class));
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

    public Producto findProducto(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Producto.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Producto> rt = cq.from(Producto.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
