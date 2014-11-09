package org.agoncal.application.petstore.service;

import org.agoncal.application.petstore.model.Item;
import org.agoncal.application.petstore.model.Product;
import org.agoncal.application.petstore.service.ProductService;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

@RunWith(Arquillian.class)
public class ProductServiceTest
{

   @Inject
   private ProductService productservice;

   @Deployment
   public static JavaArchive createDeployment()
   {
      return ShrinkWrap.create(JavaArchive.class)
            .addClass(AbstractService.class)
            .addClass(ProductService.class)
            .addClass(Product.class)
            .addAsManifestResource("META-INF/persistence.xml", "persistence.xml")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
   }

   @Test
   public void should_be_deployed()
   {
      Assert.assertNotNull(productservice);
   }

   @Test
   public void should_crud()
   {
      // Gets all the objects
      int initialSize = productservice.listAll().size();

      // Creates an object
      Product product = new Product();
      product.setName("Dummy value");

      // Inserts the object into the database
      product = productservice.persist(product);
      assertNotNull(product.getId());
      assertEquals(initialSize + 1, productservice.listAll().size());

      // Finds the object from the database and checks it's the right one
      product = productservice.findById(product.getId());
      assertEquals("Dummy value", product.getName());

      // Updates the object
      product.setName("A new value");
      product = productservice.merge(product);

      // Finds the object from the database and checks it has been updated
      product = productservice.findById(product.getId());
      assertEquals("A new value", product.getName());

      // Deletes the object from the database and checks it's not there anymore
      productservice.remove(product);
      assertEquals(initialSize, productservice.listAll().size());
   }
}
