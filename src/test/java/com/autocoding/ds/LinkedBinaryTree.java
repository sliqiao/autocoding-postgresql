package com.autocoding.ds;

/**
 * 
 * 二叉树 数据结构(链表实现)
 *
 * @param <T>
 */
public class LinkedBinaryTree<T> {
	private Node<T> root = null;

	private static class Node<T> {
		public Node<T> left;
		public Node<T> right;
		public T data;

		public Node(T data) {
			this.data = data;
		}
	}

	public static void main(String args[]) {
		final LinkedBinaryTree<String> binaryTree = LinkedBinaryTree.createBinaryTress();
		System.out.print("先序遍历二叉树:");
		binaryTree.preTraverse(binaryTree.root);
		System.out.println();
		System.out.print("中序遍历二叉树:");
		binaryTree.midTraverse(binaryTree.root);
		System.out.println();
		System.out.print("后序遍历二叉树:");
		binaryTree.postTraverse(binaryTree.root);
		System.out.println();
	}

	public static LinkedBinaryTree<String> createBinaryTress() {
		final LinkedBinaryTree<String> binaryTree = new LinkedBinaryTree<String>();
		final Node<String> root = binaryTree.createRoot("根节点");
		final Node<String> n1 = binaryTree.add(root, "n1", true);
		final Node<String> n2 = binaryTree.add(root, "n2", false);
		final Node<String> n3 = binaryTree.add(n1, "n3", true);
		final Node<String> n4 = binaryTree.add(n1, "n4", false);
		final Node<String> n5 = binaryTree.add(n2, "n5", true);
		final Node<String> n6 = binaryTree.add(n2, "n6", false);
		return binaryTree;
	}

	/**
	 * 创建二叉树的根节点
	 * @param value 根节点的值
	 */
	public Node<T> createRoot(T value) {
		if (value == null) {
			throw new RuntimeException("头节点为空");
		}
		final Node<T> newNode = new Node<T>(value);
		this.root = newNode;
		return this.root;
	}

	/**
	 * 添加节点
	 * @param parentNode 父节点
	 * @param value 添加节点的元素值
	 * @param left 是否为左节点
	 * @return 返回添加的新节点
	 */
	public Node<T> add(Node<T> parentNode, T value, boolean left) {
		if (value == null) {
			throw new RuntimeException("子节点为空");
		}
		if (parentNode == null) {
			throw new RuntimeException("父节点为空");
		}
		final Node<T> newNode = new Node<T>(value);
		if (left) {
			if (parentNode.left != null) {
				throw new RuntimeException("已有左子节点");
			}
			parentNode.left = newNode;
			return newNode;
		} else {
			if (parentNode.right != null) {
				throw new RuntimeException("已有右子节点");
			}
			parentNode.right = newNode;
			return newNode;
		}

	}

	public void preTraverse(Node<T> node) {
		System.out.print(node.data);
		System.out.print(",");
		if (node.left != null) {
			this.preTraverse(node.left);
		}

		if (node.right != null) {
			this.preTraverse(node.right);
		}

	}

	public void midTraverse(Node<T> node) {

		if (node.left != null) {
			this.midTraverse(node.left);
		}
		System.out.print(node.data);
		System.out.print(",");
		if (node.right != null) {
			this.midTraverse(node.right);
		}

	}

	public void postTraverse(Node<T> node) {
		if (node.left != null) {
			this.postTraverse(node.left);
		}
		if (node.right != null) {
			this.postTraverse(node.right);
		}

		System.out.print(node.data);
		System.out.print(",");
	}

}
